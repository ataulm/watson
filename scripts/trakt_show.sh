#!/bin/bash
source ../gradle.properties;
curl --header "trakt-api-version: 2" --header "trakt-api-key: $traktApiKey" -X GET https://api-v2launch.trakt.tv/shows/game-of-thrones?extended=images,full | python -mjson.tool