#!/bin/sh
rm -f testSuite1.zip
cd testSuite1
zip -rq ../testSuite1.zip .
cd ..
curl -F updateSpecification=true -F showIdentifiers=false -F specification=4F4410E1X430DX4E1BXB064X0CECA6A3C0D4 -F testSuite=@testSuite1.zip --header "ITB_API_KEY: D355B62BXE40BX494FX8B0AXEA8E202CAD8A" -X POST http://localhost:9000/api/rest/testsuite/deploy