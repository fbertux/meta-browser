# Copyright (C) 2009-2013, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify libvpx virtual/libgl nss nspr"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=f7e14664a6dca6a06efe93d70f711c0e"

SRC_URI = "ftp://ftp.mozilla.org/pub/mozilla.org/firefox/releases/${PV}/source/firefox-${PV}.source.tar.bz2;name=archive \
           file://mozilla-${BPN}.png \
           file://mozilla-${BPN}.desktop \
           file://vendor.js"

SRC_URI[archive.md5sum] = "aced0ee58484b433f4748111d59c9df2"
SRC_URI[archive.sha256sum] = "85eb5f916aa47a92cf50803dcb35e7fa3a9da29abf5fc91cbb6a70bbbc2618ff"

S = "${WORKDIR}/mozilla-esr38"

inherit mozilla

EXTRA_OEMAKE = "installdir=${libdir}/${PN}"

ARM_INSTRUCTION_SET = "arm"

do_install() {
	oe_runmake DESTDIR="${D}" destdir="${D}" install
	install -d ${D}${datadir}/applications
	install -d ${D}${datadir}/pixmaps
	install -m 0644 ${WORKDIR}/mozilla-${PN}.desktop ${D}${datadir}/applications/
	install -m 0644 ${WORKDIR}/mozilla-${PN}.png ${D}${datadir}/pixmaps/
	install -m 0644 ${WORKDIR}/vendor.js ${D}${libdir}/${PN}/defaults/pref/
	rm -f ${D}${libdir}/${PN}/TestGtkEmbed
	rm -f ${D}${libdir}/${PN}/defaults/pref/firefox-l10n.js

	# use locale settings
	grep -Rl intl.locale.matchOS ${D}${libdir}/${PN}/ \
	   | grep '.js$' \
	   | xargs sed -i 's/\(pref("intl.locale.matchOS",\s*\)false)/\1true)/g'

	# disable application updating
	grep -Rl app.update.enabled ${D}${libdir}/${PN}/ \
	   | grep '.js$' \
	   | xargs sed -i 's/\(pref("app.update.enabled",\s*\)true)/\1false)/g'
}

PACKAGES += "${PN}-inspector"

FILES_${PN}-inspector = "${libdir}/${PN}/chrome/inspector* \
                         ${libdir}/${PN}/components/*nspector* \
                         ${libdir}/${PN}/extensions/inspector* \
                         ${libdir}/${PN}/defaults/preferences/inspector*"
FILES_${PN} = "${bindir}/${PN} \
               ${datadir}/applications/ \
               ${datadir}/pixmaps/ \
               ${libdir}/${PN}/* \
               ${libdir}/${PN}/.autoreg \
               ${bindir}/defaults"
FILES_${PN}-dev += "${datadir}/idl ${bindir}/${PN}-config ${libdir}/${PN}-devel-*"
FILES_${PN}-staticdev += "${libdir}/${PN}-devel-*/sdk/lib/*.a"
FILES_${PN}-dbg += "${libdir}/${PN}/.debug \
                    ${libdir}/${PN}/*/.debug \
                    ${libdir}/${PN}/*/*/.debug \
                    ${libdir}/${PN}/*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/*/.debug \
                    ${bindir}/.debug"

# We don't build XUL as system shared lib, so we can mark all libs as private
PRIVATE_LIBS = "libmozjs.so \
                libxpcom.so \
                libnspr4.so \
                libxul.so \
                libmozalloc.so \
                libplc4.so \
                libplds4.so"

# mark libraries also provided by nss as private too
PRIVATE_LIBS += " \
    libfreebl3.so \
    libnss3.so \
    libnssckbi.so \
    libsmime3.so \
    libnssutil3.so \
    libnssdbm3.so \
    libssl3.so \
    libsoftokn3.so \
"
