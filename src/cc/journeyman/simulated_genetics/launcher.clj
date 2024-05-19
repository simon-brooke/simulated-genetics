(ns cc.journeyman.simulated-genetics.launcher
  (:require [clojure.tools.cli :refer [parse-opts]]
            [jme-clj.core :refer [add-control add-light-to-root add-to-root app-settings attach-child bitmap-text box cam
                                  defsimpleapp detach-all-child fly-cam geo get* get-height-map gui-node image
                                  image-based-height-map light load-font load-height-map
                                  load-model load-texture material root-node rotate scale set* start
                                  terrain-lod-control terrain-quad vec3]]
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

(def models (atom {}))

(def cli-options
  "I haven't yet thought out what command line arguments (if any) I need.
   This is a placeholder."
  [["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :update-fn inc]
   ["-h" "--help"]])

(defn init
  ;; cribbed from jme-clj examples/hello-asset.clj
  []
  (let [root-node   (root-node)
        mat-default (material "Common/MatDefs/Misc/ShowNormals.j3md")
        ;; teapot      (load-model "Models/Teapot/Teapot.obj")
        ;; teapot      (set* teapot :material mat-default)
        ;; root-node   (attach-child root-node teapot)
        ;; Create a wall with a simple texture from test_data
        ;; box         (box 2.5 2.5 1.0)
        ;; mat-brick   (material "Common/MatDefs/Misc/Unshaded.j3md")
        ;; texture     (load-texture "Textures/Terrain/BrickWall/BrickWall.jpg")
        ;; mat-brick   (set* mat-brick :texture "ColorMap" texture)
        ;; wall        (geo "Box" box)
        ;; wall        (-> wall (set* :material mat-brick) (set* :local-translation 2.0 -2.5 0.0))
        ;; root-node   (attach-child root-node wall)
        ;; Display a line of text with a default font
        gui-node    (detach-all-child (gui-node))
        gui-font    (load-font "Interface/Fonts/Default.fnt")
        size        (-> gui-font (get* :char-set) (get* :rendered-size))
        hello-text  (bitmap-text gui-font false)
        hello-text  (-> hello-text
                        (set* :size size)
                        (set* :text "Hello World")
                        (set* :local-translation 300 (get* hello-text :line-height) 0))]
    (attach-child gui-node hello-text)
    ; Load a model from test_data (OgreXML + material + texture)
    (let [model (load-model "model-prototypes/female.glb")]
      (swap! models assoc :model model)
      (println (format "Model is of type `%s`" (type model)))
      (-> model
          (scale 0.05 0.05 0.05)
          (rotate 0.0 -3.0 0.0)
          (set* :local-translation 0.0 -5.0 -2.0))
      (attach-child root-node model))
    ;; You must add a light to make the model visible
    (-> (light :directional)
        (set* :direction (vec3 -0.1 -0.7 -1.0))
        (add-light-to-root))))


(defsimpleapp app :init init)

(defn -main
  "Start an app into which generated characters can ultimately be rendered."
  [& args]
  (let [options (parse-opts args cli-options)]
    (set-min-level!
     (nth [:error :warn :debug :trace] (:verbosity (:options options)))))

  (trace! (start app)))
