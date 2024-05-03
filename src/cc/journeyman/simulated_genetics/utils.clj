(ns cc.journeyman.simulated-genetics.utils
  (:require [taoensso.telemere :refer [error! trace!]]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; Utility functions.
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

(def ^:const bits-in-genome
  "Number of bits we're actually using. **NOTE THAT** as this implementation
   is based on Java longs, this number must not be more than 63 or else we've
   *a lot* of rewriting to do."
  32)


(defn long-from-binary-string 
  "Mainly for testing, create a long from this binary string. The string is 
   expected to comprise zeros and ones, only."
  [s]
  (if (every? #{\0 \1} s)
    (Long/parseLong s 2)
    (throw (ex-info "Not a binary string" {:s s}))))

(defn create-binary-string-mask 
  "Mainly for testing, create a binary string mask with those bits indexed
   from `start` (inclusive) to `end` (exclusive) set, and all others cleared."
  [start end]
  (apply str (concat
              (repeat start "0")
              (repeat (- end start) "1")
              (repeat (- bits-in-genome end) "0")
              )))

(defn create-mask
  "Create a with those bits indexed from `start` (inclusive) to `end` (exclusive)
   set, and all others cleared."
  ;; TODO TODO: This **really** needs not to go via string representation!
  [start end]
  (let [s (trace! (create-binary-string-mask start end))]
    (long-from-binary-string s)))
