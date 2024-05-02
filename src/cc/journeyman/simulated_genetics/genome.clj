(ns cc.journeyman.simulated-genetics.genome
  "lightweight simulation of a genome."
  (:require [cc.journeyman.simulated-genetics.utils :refer [bits-in-genome create-mask long-from-binary-string]]
            [clojure.math :refer [pow]] 
            [taoensso.telemere :refer [error! trace!]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; Genome: create, combine, and extract features from a pseudo-genome.
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


(defmacro rand-genome 
  "Create a random genome."
  []
  `(long (rand (dec (pow 2 bits-in-genome))))) ;;Long/MAX_VALUE))) ;;

(defn create-genome 
  "Create a new genome; if `father` and `mother` are passed, the result will 
   comprise bits taken randomly from those genomes."
  ([]
   (create-genome (rand-genome) (rand-genome)))
  ([^Long father ^Long mother]
   (let [mask (rand-genome)]
     (trace! (format "Parents are %s, %s; mask is %s" 
                     (Long/toBinaryString father) 
                     (Long/toBinaryString mother) 
                     (Long/toBinaryString mask)))
     (bit-or (bit-and father mask) (bit-and (bit-not mother) mask)))))

(defn extract-bits
  "Extract, as an integer left-shifted by `start`, those bits from `g` indexed
   from `start` (inclusive) to `end` (exclusive)."
  [^Long g ^Long start ^Long end]
  (let [mask (trace! (create-mask start end))]
    (bit-shift-right (bit-and g mask) (- bits-in-genome end))))

(defmacro ethnically-biased-feature-index
  [genome start end]
  `(+ (extract-bits ~genome ~start ~end)
      (if (bit-test ~genome 3) 
        (int (pow (- ~end ~start) 2)) 
        0)))


(defn expand-genome
   [^Long genome]
  {:ethnic-type (extract-bits genome 0 4)
   :skin-tone (+ (extract-bits genome 4 7) (if (bit-test genome 3) 4 0) 2)
   :freckles? (= (extract-bits genome 8 10) 3)
   :hair-colour (nth [:blonde :red :russet :cognac :chestnut :coffee :dark-brown :black]
                     (ethnically-biased-feature-index genome 11 13))
   :eye-colour (nth [:blue :hazel :russet :cognac :chestnut :coffee :dark-brown :black]
                    (ethnically-biased-feature-index genome 14 16))
   :height (+ 150 (* (extract-bits genome 17 20) 6))
   :robustness (extract-bits genome 21 23)
   :aging (extract-bits genome 24 27)
   :gender (if (bit-test genome 27) :male :female)
   ;; face stuff
   })