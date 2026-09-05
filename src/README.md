# Bromide Crawler
## Introduction
Bromide crawler is an internet indexing tool for crawling websites, collecting word usage data, collecting linking data between pages and creating a local index that can be used to search the internet.

In its current form, the tool takes as input a list of links and will output a folder at location <Output-Folder> that contains a hierarchical representation of the internet that the tool has indexed. 

Currently this tool does not crawl between domains. With the current state of the internet and AI slop websites, I have chosen to make this tool crawl only the websites I set it upon. This tool does note the links to websites outside of the domain in its running, there is just currently no mechanism for the crawler threads to pass this back to the daemon and for the daemon to use this to create new crawlers.

## Getting started 
Open the settings.json tool and update the two parameters as needed for your system. It is curious to include a contact email in the User Agent you are sending. 

Create a file to contain your URLs, in this file put one link per line with no other data. 

Run 'java BromideDaemon --Entry-URLs "PATH-TO-FILE"' to start the program

## Additional Details

The daemon will create a crawler for each unique internet domain that the urls file contains. Each crawler will send a request about every 6 seconds. The system is set up to be able to recover its progress after being reset, 

Since the system is constructing a database, it is natural that the crawlers use this database as a cache between runs to prevent repeat work. If the system is reset and the entry URLS are not changed, the system will see that the files have been indexed already, use the date and contact the server to check if the content has been updated, and just use the local data if not.

## Structure of output folder
```
/crawlerOut
├── cache
│   ├── en.wikipedia.org
│   │   └── robots.txt
│   └── store.steampowered.com
│       └── robots.txt
└── domain
    ├── en.wikipedia.org
    │   └── wiki
    │       ├── Zebras.json
    │       └── ...
    └── store.steampowered.com
        ├── .json // json with no name generated for the root link of a website with no path
        └── ... 
```
## Known issues
- https://help.steampowered.com/ and https://store.steampowered.com/ are considered different domains, creating two crawlers that are making requests to the websites servers. This could cause the crawler to make more requests to a single server then is intended for a given time period
- URLS that are encoded in html containing special characters such as ' ' are not handled correctly and result in URI errors that are logged in the console. HTML encoded urls using ‰20 work fine. IE 'https://en.wikipedia.org/wiki/Hello, world'