(defproject simulated-genetics "0.1.0-SNAPSHOT"
  :description "A lightweight simulation of genetics, for use in games only."
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License,version 2.0 or (at your option) any later version"
          :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[clj-python/libpython-clj "2.025"]
                 [com.taoensso/telemere "1.0.0-beta3"] ;; Peter Taoussanis' new replacement for Timbre
                 [jme-clj "0.1.13"] 
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.cli "1.1.230"] 
                 [org.jmonkeyengine/jme3-core "3.6.1-stable"]]
  :main ^:skip-aot cc.journeyman.simulated-genetics.launcher
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
