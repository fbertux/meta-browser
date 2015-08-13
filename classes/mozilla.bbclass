SECTION = "x11/utils"
DEPENDS += "gnu-config-native virtual/libintl libxt libxi \
	    zip-native gtk+ orbit2 libidl-native"

SRC_URI += "file://mozconfig"

inherit gettext pkgconfig

EXTRA_OECONF = "--target=${TARGET_SYS} --prefix=${prefix}"
SELECTED_OPTIMIZATION = "-Os -fsigned-char -fno-strict-aliasing"

export CROSS_COMPILE = "1"

export MOZCONFIG = "${WORKDIR}/mozconfig"
export OBJDIR = "${S}/firefox-build-dir"
export MOZ_OBJDIR = "${S}/firefox-build-dir"
export FOUND_MOZCONFIG = "${WORKDIR}/mozconfig"

export TARGET_SYS
export STAGING_INCDIR
export STAGING_LIBDIR
export PKG_CONFIG_PATH

export CONFIGURE_ARGS = "${EXTRA_OECONF}"
export HOST_CC = "${BUILD_CC}"
export HOST_CXX = "${BUILD_CXX}"
export HOST_CFLAGS = "${BUILD_CFLAGS}"
export HOST_CXXFLAGS = "${BUILD_CXXFLAGS}"
export HOST_LDFLAGS = "${BUILD_LDFLAGS}"
export HOST_RANLIB = "${BUILD_RANLIB}"
export HOST_AR = "${BUILD_AR}"

mozilla_do_configure() {
	# Put PARALLEL_MAKE into mozconfig
	if [ ! -z "${PARALLEL_MAKE}" ] ; then
		echo mk_add_options MOZ_MAKE_FLAGS=\"${PARALLEL_MAKE}\" \
			>> ${MOZCONFIG}
	fi
    # Preserve configure.in timestamp to avoid autoconf be executed.
    touch -t 06241828 configure.in
    oe_runmake -f client.mk -s configure
}

mozilla_do_compile() {
	oe_runmake -f client.mk build
}

mozilla_do_install() {
	oe_runmake -f client.mk DESTDIR="${D}" INSTALL_SDK= install
}

EXPORT_FUNCTIONS do_configure do_compile do_install
