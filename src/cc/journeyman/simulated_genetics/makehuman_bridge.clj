(ns cc.journeyman.simulated-genetics.makehuman-bridge
  "Bridge to MakeHuman, in an attempt to use it to generate character models.
   
   **NOTE**: Currently not under active development. I've failed to get this
   to work, but, even if I succeeded, it would be a very complex and fragile
   solution."
  (:require [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python
             :refer [as-python as-jvm
                     ->python ->jvm
                     get-attr call-attr call-attr-kw
                     get-item initialize!
                     run-simple-string
                     add-module module-dict
                     import-module
                     py. py.. py.-
                     python-type
                     ;; dir
                     ]
             :as py]
            [taoensso.telemere :refer [error! trace!]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; Bridge to MakeHuman, in an attempt to use it to generate character models.
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

(defn initialise-makehuman!
  "Initialise the local instance of MakeHuman. `mh-path` should be a valid
   path to the directory in which MakeHuman is installed, i.e. the directory
   which contains `makehuman.py`."
  [^String mh-path]
  (initialize!)
  (map #(trace! (run-simple-string %))
       ["import sys"
        (format "sys.path.append('%s')" mh-path)
        (format "exec(open('%s/makehuman.py').read())" mh-path)
        "from lib.core import G" 
        "G.app.mhapi.internals.getHuman()" 
        ;; fails here with "AttributeError: 'NoneType' object has no attribute 'mhapi'"
        ]))