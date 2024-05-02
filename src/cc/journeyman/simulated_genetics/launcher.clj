(ns cc.journeyman.simulated-genetics.launcher
  (:require [clojure.tools.cli :refer [parse-opts]]
            [jme-clj.core :refer [add-control add-to-root app-settings cam
                                  defsimpleapp fly-cam get-height-map image
                                  image-based-height-map load-height-map
                                  load-texture material set* start
                                  terrain-lod-control terrain-quad]]
            [taoensso.telemere :refer [set-min-level! trace!]])
  (:import (com.jme3.texture Texture$WrapMode))
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; Launcher: parses any command line options, and launches the test app.
;;;;
;;;; This program is free software; you can redistribute it and/or
;;;; modify it under the terms of the GNU General Public License
;;;; as published by the Free Software Foundation; either version 2
;;;; of the License, or (at your option) any later version.
;;;;
;;;; This program is distributed in the hope that it will be useful,
;;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;;; GNU General Public License for more details.
;;;;
;;;; You should have received a copy of the GNU General Public License
;;;; along with this program; if not, write to the Free Software
;;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
;;;; USA.
;;;;
;;;; Copyright (C) 2024 Simon Brooke
;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare app)

(def cli-options
  "I haven't yet thought out what command line arguments (if any) I need.
   This is a placeholder."
  [["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :update-fn inc]
   ["-h" "--help"]])

(defn init
  "Again, placeholder. This initialises a bit of standard jMonkeyEngine 
   terrain, just to check I have things wired up correctly."
  []
  (set* (fly-cam) :move-speed 50)
  (let [grass (set* (load-texture "jme3/textures/terrain/splat/grass.jpg")
                    :wrap Texture$WrapMode/Repeat)
        dirt (set* (load-texture "jme3/textures/terrain/splat/dirt.jpg")
                   :wrap Texture$WrapMode/Repeat)
        rock (set* (load-texture "jme3/textures/terrain/splat/road.jpg")
                   :wrap Texture$WrapMode/Repeat)
        mat (material "Common/MatDefs/Terrain/Terrain.j3md")
        height-map-tex (load-texture
                        "jme3/textures/terrain/splat/mountains512.png")
        height-map (->> height-map-tex image image-based-height-map
                        load-height-map)
        patch-size 65
        terrain (terrain-quad "my terrain" patch-size 513
                              (get-height-map height-map))]
    (-> mat
        (set* :texture "Alpha"
              (load-texture "jme3/textures/terrain/splat/alphamap.png"))
        (set* :texture "Tex1" grass)
        (set* :float "Tex1Scale" (float 64))
        (set* :texture "Tex2" dirt)
        (set* :float "Tex2Scale" (float 32))
        (set* :texture "Tex3" rock)
        (set* :float "Tex3Scale" (float 128)))
    (-> terrain
        (set* :material mat)
        (set* :local-translation 0 -100 0)
        (set* :local-scale 2 1 2)
        (add-to-root)
        (add-control (terrain-lod-control terrain (cam))))))

(defsimpleapp app :init init)

(defn -main
  "Start an app into which generated characters can ultimately be rendered."
  [& args]
  (let [options (parse-opts args cli-options)]
    (set-min-level!
     (nth [:error :warn :debug :trace] (:verbosity (:options options)))))

  (trace! (start app)))
