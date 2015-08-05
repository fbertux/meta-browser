# Copyright (C) 2009-2013, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify libvpx \
            virtual/libgl nss nspr icu pulseaudio yasm-native"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=f7e14664a6dca6a06efe93d70f711c0e"

SRC_URI = "ftp://ftp.mozilla.org/pub/mozilla.org/firefox/releases/${PV}/source/firefox-${PV}.source.tar.bz2;name=archive \
           file://mozilla-${BPN}.png \
           file://mozilla-${BPN}.desktop \
           file://vendor.js \
           file://fix-python-path.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           file://prefs/Set-javascript.options.showInConsole.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/Avoid-spurious-Run-items-in-application-handlers.patch \
           file://fixes/Bug-1136958-Remove-duplicate-SkDiscardableMemory_none.patch \
           file://fixes/Bug-1165654-Cleanup-how-libjpeg-turbo-assembly-build.patch \
           file://fixes/Bug-1166243-Remove-build-function-from-js-and-xpc.patch \
           file://fixes/Bug-1166538-Use-dozip.py-for-langpacks.patch \
           file://fixes/Bug-1094324-Set-browser.newtabpage.enhanced-default.patch \
           file://fixes/Bug-1168231-Normalize-file-mode-in-jars.patch \
           file://fixes/Bug-1168316-Remove-build-machine-name-from-about.patch \
           file://fixes/Bug-1098343-part-1-support-sticky-preferences-meaning.patch \
           file://fixes/Update-patch-from-bug-1094324-to-fit-what-landed.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications.patch \
           file://debian-hacks/Don-t-register-plugins-if-the-MOZILLA_DISABLE_PLUGINS.patch \
           file://debian-hacks/Use-a-variable-for-xulrunner-base-version-in-various.patch \
           file://debian-hacks/Don-t-error-out-when-run-time-libsqlite-is-older-than.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Load-distribution-search-plugins-from.patch \
           file://debian-hacks/Handle-transition-to-etc-appname-searchplugins.patch \
           file://debian-hacks/Preprocess-appstrings.properties.patch \
           file://debian-hacks/Disable-Firefox-Health-Report.patch \
           file://debian-hacks/Add-debian-extra-stuff-to-upstream-build-system.patch \
           file://debian-hacks/Make-upstream-build-system-create-debian-installer.patch \
           file://debian-hacks/Bump-search-engine-max-icon-size-to-35kB.patch \
           file://debian-hacks/NSS-Adds-the-SPI-Inc.-and-CAcert.org-CA-certificates.patch \
           file://debian-hacks/Work-around-binutils-assertion-on-mips.patch \
           file://iceweasel-branding/Use-MOZ_APP_DISPLAYNAME-to-fill-appstrings.properties.patch \
           file://iceweasel-branding/Modify-search-plugins-depending-on-MOZ_APP_NAME.patch \
           file://iceweasel-branding/Determine-which-phishing-shavar-to-use-depending-on.patch \
           file://iceweasel-branding/Use-firefox-instead-of-MOZ_APP_NAME-for-profile-reset.patch \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://porting/NSS-GNU-kFreeBSD-support.patch \
           file://porting/Make-powerpc-not-use-static-page-sizes-in-mozjemalloc.patch \
           "

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
