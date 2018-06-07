[![Build Status](https://travis-ci.org/rjbautis/DockstoreDOSPlugin.svg?branch=master)](https://travis-ci.org/rjbautis/DockstoreDOSPlugin)
[![Coverage Status](https://coveralls.io/repos/github/rjbautis/DockstoreDOSPlugin/badge.svg?branch=master)](https://coveralls.io/github/rjbautis/DockstoreDOSPlugin?branch=master)

# data-object-service-plugin
[Dockstore Data Object Service](https://github.com/ga4gh/data-object-service-schemas) file preprovisioning plugin

## Usage


The Data Object Service plugin fetches data objects from a provided DOS URI in order to download one of the returned URLs.

For example, if the data object for a given DOS URI references s3 and gcs URIs, the URL for the s3 data object is passed into the s3 plugin for downloading, because a gcs file plugin for Dockstore does not currently exist.

The plugin only supports downloads. Support for uploads will be added later.

```
$ cat test.dos.json
{
  "input_file": {
        "class": "File",
        "path": "dos://ec2-52-26-45-130.us-west-2.compute.amazonaws.com:8080/911bda59-b6f9-4330-9543-c2bf96df1eca"
    },
    "output_file": {
        "class": "File",
        "path": "/tmp/md5sum.txt"
    }
}

$ dockstore tool launch --entry  quay.io/briandoconnor/dockstore-tool-md5sum  --json test.dos.json
Creating directories for run of Dockstore launcher at: ./datastore//launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d
Provisioning your input files to your local machine
Preparing download location for: #input_file from dos://ec2-52-26-45-130.us-west-2.compute.amazonaws.com:8080/911bda59-b6f9-4330-9543-c2bf96df1eca into directory: ./datastore//launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d/inputs/6925e57d-ed67-4acf-ae5e-db0d987384c4
Calling on plugin io.dockstore.provision.S3Plugin$S3Provision to provision s3://1000genomes/phase3/data/HG03237/cg_data/ASM_blood/REPORTS/substitutionLengthCoding-GS000017140-ASM.tsv
Calling out to a cwl-runner to run your tool
Executing: cwltool --enable-dev --non-strict --outdir ./datastore//launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d/outputs/ --tmpdir-prefix ./datastore//launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d/tmp/ --tmp-outdir-prefix ./datastore//launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d/working/ ./Dockstore.cwl ./datastore//launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d/workflow_params.json
/Library/Frameworks/Python.framework/Versions/3.6/bin/cwltool 1.0.20170828135420
...
Provisioning your output files to their final destinations
Registering: #output_file to provision from ./dockstore/dockstore-tool-md5sum-master/datastore/launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d/outputs/md5sum.txt to : /tmp/md5sum.txt
Provisioning from ./datastore/launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d/outputs/md5sum.txt to /tmp/md5sum.txt
Downloading: file:///./datastore/launcher-2c670320-9ade-4f9d-9e54-3eff66c29e8d/outputs/md5sum.txt to file:///tmp/md5sum.txt
```
