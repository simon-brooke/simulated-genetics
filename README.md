# simulated-genetics

A clojure library (OK, at this moment it's an app, but that's during development only) to generate character models for games, such that characters who are represented as related to one another will have systematically similar appearance, as creatures of natural species (including humans) do. This is specifically **NOT** simulating genetics on any deep or quasi-scientific level, just experimenting to see how adequate a solution can be achieved with simple code and limited data.

## Status

Very pre-alpha.

## Concept

If we're going to have a world with a multi-generational population of hundreds of thousands of procedurally generated characters, and we're to persuasively represent each character as being related to others, then we have to have a mechanism for making children look reasonably like their parents, to have family resemblances among cousins, and so on. We need to do this at reasonably low data storage and algorithmic cost, firstly because we have to store all these characters, and secondly because (especially when the player approaches an urban centre), we may need to instantiate models for a lot of them in limited time.

This note discusses how this might be done.

### The pseudo-genome

Suppose we have a binary vector of memory, such that when a 'child' was born to two parents, bits were taken at random from the parents' chromosomes to populate the child's genome -- which is sort of, very roughly, what happens in actual biology -- how big would that genome have to be? After all, the full data size of the human genetic code is enormous. But actually, we don't need to simulate anything like so large. After all, all our genome needs to encode is morphology, and only sufficiently to enable the player to recognise and distinguish characters.

My hunch is that a 64 bit genome is more than sufficient, if we code it carefully. So here's how such a genome might be structured: 

| Field                | Bits | Interpretation                                               |
| -------------------- | ---- | ------------------------------------------------------------ |
| Ethnic type          | 4    | Ethnic type. Most significant bits both indicate dark skin, with [??11] indicating dark skin/curly hair and [??01] indicating dark skin/straight hair |
| Skin tone            | 3    | Plus most significant bit from ethnic type (i.e. [???1]) as most significant bit. This means  sixteen distinct tones, with the darkest tone of 'pale skinned' ethnicities just very slightly lighter than the palest tone of 'dark skinned' ethnicities. |
| Freckles?            | 2    | [11] means freckles, any other value means no freckles. Freckles won't be visible on very dark skin. |
| Hair colour          | 3    | Plus most significant bit from ethnic type (i.e. [???1]) as most significant bit. Least significant bit does not contribute to tone but indicates red tint. Thus eight distinct degrees of darkness from pale blond to black, plus red tint which can affect any degree of darkness. |
| Eye colour           | 2    | Plus most significant bit from ethnic type (i.e. [???1]) as most significant bit. Thus eight values: [000] blue; [001] hazel; [010]...[111] shades of brown lighter->darker. |
| Height               | 3    | Height when adult; children will have a scaled proportion of their adult height, and the same height value in the genome will result in female body models 95% the height of an equivalent male body model. So [000] codes for 150mm, [111] codes for 200mm, with eight distinct values |
| Gracility/Robustness | 3    | Slenderness to stockiness of skeleton/armature build, with [000] being very slender and [111] being very broad/heavy. |
| Age-related change   | 3    | People get white haired at different ages; some men go bald and some do not. The sons of the daughter of a bald man should have a chance of inheriting age-related baldness, although their mother won't express that gene. So I'm allowing here for eight different profiles for age related change, although I'm not yet clear what the exact values would mean. |

That's twenty-nine of our sixty-four bits, leaving plenty for face models, gender and so on.

### What's not included in the genome

Things which are cultural are not included in the genome; things which are lifestyle related are not included in the genome. So, for example, gracility/robustness, is not the same as skinniness/fatness, which are mostly lifestyle/diet related rather than genetic. There are some occupations (e.g., blacksmith) where you'd be unlikely to be fat (but might be very robust). Also, the same character might grow fatter (or thinner) over time. 

Similarly, hairstyle and beard-wearing are cultural (and occupational) rather than genetic, and closely related to choice of clothing. So while we do need to represent these things, they're not things which should be represented in the genome.

Injury-related change -- which would especially affect soldiers and outlaws especially but could affect any character -- also needs to be encoded somehow (and may cause real problems), but this is also not a problem for the genome.

### What additionally might be included in the pseudo-genome

There is a variable I'm proposing for non-player characters that I'm calling `disposition`, which has a range of values between -5 (surly) and +5 (sunny), which stands in for the general friendliness of the character towards random strangers, their generosity, their optimism, their degree of compassion, and so on. I don't personally believe these things are genetic -- I believe they're nurture, not nature -- but I believe that they are nevertheless inherited through families to some extent.

If we made a character's setting for `disposition` a function of their parents' dispositions, then that would need to be an entirely unrelated value from the physical appearance values, because otherwise you would end up having some racial appearances being friendlier and more optimistic than others, which would lead to accusations of racism and other bad things. This is simulated genetics, not simulated phrenology!

I mean, you may be planning the sort of game in which there are races like orcs or kobolds or whatever which are systematically less friendly and generous than characters of other races, in which case you might want to fork this library and change this decision, but if so:

a. that's on you; and 
b. I politely suggest that you might want to examine your own attitudes.

### Making this all work

**NOTE:** At this stage none of this works.

[MakeHuman](http://www.makehumancommunity.org/) exposes an [API](https://github.com/makehumancommunity/community-plugins-mhapi/blob/master/docs/MHAPI.md) which allows at least many of the morphological changes required by the pseudo-genome to be applied to a human model. 

There's a well regarded library, [libpython-clj](https://www.futurile.net/2020/02/20/python-from-clojure-with-libpython-clj/), which allows calling of Python code from Clojure code. So in theory it should be possible to make this work.

If not, there are other human-model-morphing libraries out there, e.g. [ManuelBastioniLab](https://mb-lab-community.github.io/MB-Lab.github.io/), but they're mainly also in Python. In the worst case, the heavy lifting is in the data, and it might be possible to rewrite the code thatmorphs the data into Clojure. However, development of ManuelBastioniLAB [ceased in 2018](https://www.cgchannel.com/2018/11/manuel-bastioni-to-discontinue-manuel-bastioni-lab/), and although there are forks available on GitHub and elsewhere, none of them seem to be active. 

## Installation

You can't, it doesn't (yet) work.

## Usage

FIXME: explanation

    $ java -jar simulated-genetics-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2024 Simon Brooke

This Source Code is made available under the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version, with the GNU Classpath Exception which is available at https://www.gnu.org/software/classpath/license.html.
