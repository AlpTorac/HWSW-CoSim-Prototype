#!/bin/sh

Help() 
{
    echo "/bin/sh build.sh [arg1] [arg2] ..."
    echo ""
    echo "This script builds the project with the given arguments"
    echo "described below."
    echo ""
    echo "To build the project successfully, one has to install"
    echo "the necessary packages, mosaik and the git-submodules"
    echo "beforehand. That can be achieved by running this script"
    echo "with the arguments \"package mosaik modules\". The gem5"
    echo "is not mandatory to build this project."
    echo ""
    echo "This script always attempts to builds the software simulator"
    echo "part (swsim) regardless of the arguments provided."
    echo ""
    echo "Script arguments:"
    echo ""
    echo "      package:    Install the required packages."
    echo "                  Installs gem5 related packages"
    echo "                  only if used with the \"gem5\""
    echo "                  argument"
    echo ""
    echo "      mosaik:     Make a virtual python environment"
    echo "                  for mosaik and install mosaik"
    echo ""
    echo "      modules:    Build the git submodules"
    echo ""
    echo "      gem5:       Build gem5 with the target ISA"
    echo "                  X86, the \"opt\" build option using"
    echo "                  the recommended amount of threads."
    echo "                  Note that building gem5 can take a"
    echo "                  very long time and is not mandatory"
}

# Install the required packages
#
# Passing "true" for any of the following install variables implies
# that this script has been run with its first argument set to "true" before
#
# Does not install packages required to build gem5, unless INSTALL_GEM5=true
INSTALL_REQUIRED_PACKAGES=false

# Install mosaik
#
# Required for installing the git submodules
INSTALL_MOSAIK=false

# Install the git submodules
#
# Passing "true" for this argument implies that the required packages 
# and mosaik have been installed
INSTALL_GIT_MODULES=false

# Install gem5 (can take multiple hours)
#
# Only run, if the "gem5_scenario.py" has to be run, otherwise
# leaving this variable with the value "false" is recommended
INSTALL_GEM5=false

OPTIONS=""

# Iterate all given arguments and set the corresponding variables to true
#
# Using an undefined argument calls the Help() function and returns
for arg in "$@"
do
    case ${arg} in
        package)
            INSTALL_REQUIRED_PACKAGES=true
            OPTIONS=${OPTIONS}" ${arg}"
            ;;

        mosaik)
            INSTALL_MOSAIK=true
            OPTIONS=${OPTIONS}" ${arg}"
            ;;

        modules)
            INSTALL_GIT_MODULES=true
            OPTIONS=${OPTIONS}" ${arg}"
            ;;

        gem5)
            INSTALL_GEM5=true
            OPTIONS=${OPTIONS}" ${arg}"
            ;;

        *)
            echo "Unknown build argument: ${arg}"
            Help
            return
            ;;
    esac
done

echo "Building with the options:${OPTIONS}"

if ${INSTALL_REQUIRED_PACKAGES} ; then
    # Make sure that everything is up to date
    apt -y update && apt -y upgrade

    # Install GIT
    apt install -y git

    # Install Python
    apt install -y python3-dev python3-venv pip \
    python3-numpy python3-scipy python3-h5py

    # Install Python test library
    apt-get install -y python3-pytest

    # Install Java
    apt install -y default-jdk

    # Install ant
    apt install -y ant

    # Install Maven (if not already installed)
    apt install -y maven

    # Set the environment variable for Java and
    # append it to the PATH variable, if not already done
    if [ ! "${JAVA_HOME}" ] ; then
        export JAVA_HOME=/usr/lib/jvm/default-java
        export PATH="${PATH}":${JAVA_HOME}/bin
    fi

    # Set environment variables for Maven and
    # append them to the PATH variable, if not already done
    if [ ! "${M2_HOME}" ] || [ ! "${MAVEN_HOME}" ] ; then
        export M2_HOME=/usr/share/maven
        export MAVEN_HOME=/usr/share/maven
        export PATH="${PATH}":${M2_HOME}/bin
    fi
fi

if ${INSTALL_MOSAIK} ; then
    # Setup a Python environment, if not already done
    if [ ! "${PY_ENV}" ] ; then
        PY_ENV_PATH="/opt/venv"
        export PY_ENV=${PY_ENV_PATH}
        python3 -m venv --system-site-packages ${PY_ENV}
        export PATH="${PY_ENV}/bin:${PATH}"
    fi

    # Install mosaik
    pip install mosaik
    pip install mosaik-api
fi

# Get the absolute path
BASE_DIR=$( pwd )

# Construct the absolute path to the git-modules directory
GIT_MODULE_PATH=${BASE_DIR}"/git-modules"

# Build gem5 for the desired target ISA with the chosen
# build option using the recommended amount of cores.
#
# See https://www.gem5.org/documentation/learning_gem5/part1/building/
# For further details
# -----------------------------------------------------------------------------
# | WARNING : This can take a very long time to build depending on the machine|
# -----------------------------------------------------------------------------
if ${INSTALL_GEM5} ; then
    # Install packages required to build gem5
    apt -y install build-essential m4 \
    scons zlib1g zlib1g-dev libprotobuf-dev protobuf-compiler \
    libprotoc-dev libgoogle-perftools-dev doxygen \
    libboost-all-dev libhdf5-serial-dev python3-pydot libpng-dev \
    libelf-dev pkg-config black valgrind

    # The instruction set that gem5 will model
    GEM5_TARGET_ISA="X86"

    # The gem5 binary type, either "opt", "debug" or "fast"
    GEM5_BUILD_OPTION="opt"

    # Get the recommended core count for building gem5
    NUM_CORES=$(( $( nproc --all ) + 1 ))

    # Start building gem5
    cd "${GIT_MODULE_PATH}/gem5" || return
    pip install mypy pre-commit
    scons build/${GEM5_TARGET_ISA}/gem5.${GEM5_BUILD_OPTION} -j ${NUM_CORES}

    # Go back to the base directory
    cd "${BASE_DIR}" || return
fi

if ${INSTALL_GIT_MODULES} ; then
    # Clean and build the DFA library
    mvn clean install -f "${GIT_MODULE_PATH}/Automata/pom.xml"

    # Clean and build the JavaSim library
    mvn clean install -f "${GIT_MODULE_PATH}/JavaSim/pom.xml"

    # Clean and build the mosaik API for Java
    ant -f "${GIT_MODULE_PATH}/mosaik-api-java/build.xml"
fi

# Locate the jar files of the built git submodules
DFA_DEPENDENCY=$(find "${GIT_MODULE_PATH}" -type f -iname "automata*.jar")
JAVASIM_DEPENDENCY=$(find "${GIT_MODULE_PATH}" -type f -iname "javasim*.jar")
MOSAIK_DEPENDENCY=$(find "${GIT_MODULE_PATH}" -type f -iwholename "*dist/mosaik-api-java*.jar")
JSON_SIMPLE_DEPENDENCY=$(find "${GIT_MODULE_PATH}" -type f -iwholename "*dist/json-simple*.jar")

# Clean and build the software simulator
mvn clean install -f "swsim/pom.xml" \
"-Dautomata.path=${DFA_DEPENDENCY}" \
"-Djavasim.path=${JAVASIM_DEPENDENCY}" \
"-Dmosaik.path=${MOSAIK_DEPENDENCY}" \
"-Djson.simple.path=${JSON_SIMPLE_DEPENDENCY}"