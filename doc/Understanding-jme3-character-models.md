# Understanding how jME3 handles character models

(This note is largely based on analysis of Stephen Gold's [Maud](https://github.com/stephengold/Maud)).

## Dem Bones

One potential way to vary character models is to adjust the length of their bones. In order to be able to do this, I need to get hold of them. This note documents how to get hold of them in the [jMonkeyEngine](https://jmonkeyengine.org/) 3 API (referenced as 'jME3').

Model files (including, but not limited to, `.j3o` files) are [loaded by the AssetManager](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/asset/AssetManager.html#loadModel(com.jme3.asset.ModelKey)) as instances of [Spatial](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/scene/Spatial.html). A spatial can be any sort of scene node -- a building, a tree, a rock, a container. It is not inherently a character and does not inherently have a skeleton or a rig; furthermore, the abstract class Spatial has no methods to extract a skeleton or a rig -- or even, actually, a geometry, material or texture.

Maud loads models as instances of `maud.model.cgm.LoadedCgm`, which wraps the Spatial. 

[Instance variable declaration](https://github.com/stephengold/Maud/blob/master/src/main/java/maud/model/cgm/Cgm.java#L205):
```java
    /**
     * root spatial in the MVC model's copy of the C-G model
     */
    protected Spatial rootSpatial = null;
```

[Instantiation](https://github.com/stephengold/Maud/blob/master/src/main/java/maud/model/cgm/LoadedCgm.java#L292):
```java
        this.rootSpatial = Heart.deepCopy(cgmRoot);
```

`LoadedCgm` is a subclass of [`Cgm`](https://github.com/stephengold/Maud/blob/master/src/main/java/maud/model/cgm/Cgm.java), where 'CGM' is stated in the documentation to be an acronym for 'Computer Graphics Model'.

The Cgm class has an instance variable `selectedSkeleton` which is instantiated at the time the Cgm instance is constructed to a new, empty, instance of [SelectedSkeleton](https://github.com/stephengold/Maud/blob/master/src/main/java/maud/model/cgm/SelectedSkeleton.java). So how does the SelectedSkeleton instance (which is instantiated before the `rootSpatial` is set) get to know about the skeleton from the Spatial, which has, inherently, no skeleton? The answer is that is calls the [`countBones()`](https://github.com/stephengold/Maud/blob/master/src/main/java/maud/model/cgm/SelectedSkeleton.java#L148) method of the selectedSkeleton:

```java
    public int countBones() {
        int result = 0;
        Object selected = find();
        if (selected instanceof Armature) {
            result = ((Armature) selected).getJointCount();
        } else if (selected instanceof Skeleton) {
            result = ((Skeleton) selected).getBoneCount();
        }

        assert result >= 0 : result;
        return result;
    }
```

Part of the complexity here is backwards compatibility. The class [`Armature`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/anim/Armature.html) is a newer replacement for the older (and now deprecated) class [`Skeleton`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/animation/Skeleton.html); this appears to be part of a major re-engineering of how jME3 handles animation.

and `countBones()` calls [`find()`](https://github.com/stephengold/Maud/blob/master/src/main/java/maud/model/cgm/SelectedSkeleton.java#L253):

```java
    /**
     * Find the selected Armature or Skeleton.
     *
     * @return the pre-existing instance, or null if none
     */
    Object find() {
        Object result = find(null);
        return result;
    }
```

which in turn calls [`find(binary[])`](https://github.com/stephengold/Maud/blob/master/src/main/java/maud/model/cgm/SelectedSkeleton.java#L181):

```java
    /**
     * Find the selected Armature or Skeleton.
     *
     * @param storeSelectedSgcFlag if not null, set the first element to true if
     * the skeleton came from the selected S-G control or its controlled
     * spatial, false if it came from the C-G model root
     * @return a pre-existing Armature or Skeleton, or null if none selected
     */
    Object find(boolean[] storeSelectedSgcFlag) {
        boolean selectedSgcFlag;
        Object skeleton = null;
        /*
         * If the selected S-G control is an AnimControl, SkeletonControl,
         * or SkinningControl, use its skeleton, if it has one.
         */
        Control selectedSgc = cgm.getSgc().get();
        if (selectedSgc instanceof AnimControl) {
            skeleton = ((AnimControl) selectedSgc).getSkeleton();
        }
        if (skeleton == null && selectedSgc instanceof SkeletonControl) {
            skeleton = ((SkeletonControl) selectedSgc).getSkeleton();
        }
        if (skeleton == null && selectedSgc instanceof SkinningControl) {
            skeleton = ((SkinningControl) selectedSgc).getArmature();
        }
        ...
```

And so on. 

I'm going to confess here that coming back to object oriented programming after a decade of concentrating on functional programming, it's frustrating how complicated, messy and repetitious it is. But in this instance I can't help feeling that it would have been less messy if the abstract class `CGM` had a method `getSkeleton()` which by default returned null; and which was overridden in subclasses of `CGM` which represented things which did have skeletons to return their skeletons.

But this only kicks the 'how to get the skeleton' one step further down the road, to [`Control`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/scene/control/Control.html). `Control` also wraps a `Spatial`, which also isn't instantiated at construction time, and also doesn't have a `getSkeleton()` method.

To be fair I don't know what proportion of subclasses of `Control` have skeletons, but on the evidence here at least four do; and an overridable instance method on [`AbstractControl`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/scene/control/AbstractControl.html) returning `null`, declared on the [`Control`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/scene/control/Control.html) interface, would have little cost and save a lot of mess.

As there are now a lot of branches to cover, I'm going to concentrate on the `SkinningControl` one, which *seems* to be the current state of the art. I haven't at this stage investigated how `AssetManager.loadModel(String)` determines which classes to instantiate when loading a model, but I'm going to assume that I can coerce my models to be loaded in a non-deprecated form.

(Confirmed: I am getting instances of SkinningControls when I load models).

A [`SkinningControl`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/anim/SkinningControl.html) has a private instance variable `armature`:

```java
    /**
     * The armature of the model.
     */
    private Armature armature;
```

which is instantiated in the constructor:

```java
    /**
     * Creates an armature control. The list of targets will be acquired
     * automatically when the control is attached to a node.
     *
     * @param armature the armature
     */
    public SkinningControl(Armature armature) {
        if (armature == null) {
            throw new IllegalArgumentException("armature cannot be null");
        }
        this.armature = armature;
        this.numberOfJointsParam = new MatParamOverride(VarType.Int, "NumberOfBones", null);
        this.jointMatricesParam = new MatParamOverride(VarType.Matrix4Array, "BoneMatrices", null);
    }
```

the `Armature` class has an instance method [`getJointCount()`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/anim/Armature.html#getJointCount()); it also has instance methods [`getJointList()`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/anim/Armature.html#getJointList()), [`getJoint(int)`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/anim/Armature.html#getJoint(int)), and [`getJoint(String)`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/anim/Armature.html#getJoint(java.lang.String)).

**Note that** `Joint` *seems* to be a name used in the rewrite of the animation system to avoid confusion with the `Bone` in the earlier animation system, and *I think* represents what normal animation rig nomenclature would refer to as a bone. 

However, the `Joint` class has no `length` instance variable. What it has is a `targetGeometry` instance variable which is declared as a [`Geometry`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/scene/Geometry.html). Geometry, in turn, has no dimensions, but has an instance variable `mesh` declared as a [`Mesh`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/scene/Mesh.html). I suspect that it is the Mesh object -- which contains lines, triangles and vertices -- which provides the actual dimensioned objects.

I don't think, however, that I either need to or should change anything in the Geometry objects themselves. Instead, the Joint object also has three instance variables bound to instances of [`Transform`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/math/Transform.html):

* `localTransform`;
* `initialTransform`;
* `jointModelTransform`;

Each of these is private, and has a getter but no setter. It is my hypothesis that to alter the length of the bone, one should make its `localTransform` IV a scaling transform, by calling its `setScale(float)` method. There are alternate signatures to this method, one taking three floats, one for each coordinate, and the other taking an instance of [`Vector3f`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/math/Vector3f.html); it may be that one of these would be preferable because for my purposes I'm only interested in varing the length.

## Skin

In order to change the overall skin colour of a character, we have to modify the [`Material`](https://javadoc.jmonkeyengine.org/v3.6.1-stable/com/jme3/material/Material.html) or the `Texture` of the skin; 

Material has a method `setColor(String, ColorRGBA)` which should do the trick; and every Geometry has a Material. The Geometry class is a subclass of Spatial, so if the Spatial returned by `AssetManager.loadNodel(String)` is an instance of Geometry, which I believe it will be, we're *probably* golden. If not, the `Cgm` class has a mechanism for getting the Texture, but not the Material, of the Spatial, and I'll have to explore that route.

Broadly, I think that the material of the skin should be acted on by the genome (to set overall colour), while the texture may be acted on by some mechanism for handling acquired characteristics (to handle scars, amputations, tattoos, etc).

## Eyes, hair

I need to be able to set eye colour, eyebrow shape and colour, hair colour, and baldness from the genome (modulated by age). The hair **style** should not be set from the genome but from acquired characteristics. I'm assuming that hair dyes are not a thing in the in-game culture, otherwise hair colour would also have to be in acquired characteristics.