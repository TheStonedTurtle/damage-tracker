#!/bin/bash

# Copyright (c) 2019 Abex
# Copyright (c) 2019 TheStonedTurtle
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this
#    list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
# ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

PLUGIN_ID="damage-tracker"
RUNELITE_VERSION="1.6.0.1"

# check valid plugin id
[[ $PLUGIN_ID =~ ^[a-z0-9-]+$ ]]

SCRIPT_HOME="$(cd "$(dirname "$0")" ; pwd -P)"

# we must have a full 40 char sha1sum
[[ $TRAVIS_COMMIT =~ ^[a-fA-F0-9]{40}+$ ]]

BUILDDIR="$(mktemp -d /tmp/external-plugin.XXXXXXXX)"
trap "rm -rf ""$BUILDDIR""" EXIT

SIGNING_KEY="" REPO_CREDS="" gradle \
	--no-build-cache \
	--parallel \
	--console=plain \
	--init-script="$SCRIPT_HOME/package.gradle" \
	-DrlpluginRuneLiteVersion="$RUNELITE_VERSION" \
	-DrlpluginOutputDirectory="$BUILDDIR" \
	-DrlpluginPluginID="$PLUGIN_ID" \
	-DrlpluginCommit="$TRAVIS_COMMIT" \
	rlpluginPackageJar rlpluginEmitManifest || exit 1

echo "Build Success"
