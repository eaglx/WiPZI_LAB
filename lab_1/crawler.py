import urllib.request as req
import sys
import os
from html.parser import HTMLParser
import numpy

#-------------------------------------------------------------------------

class FIFO_Policy:
    def __init__(self, c):
        self.queue = [s for s in c.seedURLs]

    def getURL(self, c, iteration):
        if (len(self.queue) == 0):
            #return None
            self.queue = [s for s in c.seedURLs]
        url = self.queue[0]
        self.queue.pop(0)
        return url

    def updateURLs(self, c, newURLs, newURLsWD, iteration):
        tmpList = [url for url in newURLs]
        tmpList.sort(key = lambda url: url[len(url) - url[::-1].index('/'):])
        for url in tmpList:
            self.queue.append(url)

class LIFO_Authority_Policy:
    def __init__(self, c):
        self.queue = [s for s in c.seedURLs]
        self.fetched = []
        self.authority = {}

    def getURL(self, c, iteration): # TODO ############################
        while True:
            if (len(self.queue) == 0):
                break
            elif self.queue[-1] in self.fetched:
                self.queue.pop(-1)
            else:
                break

        if (len(self.queue) == 0):
            if(len(c.incomingURLs) != 0):
                for k, v in c.incomingURLs.items():
                    self.authority[k] = len(v) + 1
                for k, v in c.incomingURLs.items():
                    for vi in v:
                        if vi not in self.authority:
                            self.authority[vi] = 1
            list_one = []
            list_two = []
            for k, v in self.authority.items():
                list_one.append(k)
                list_two.append(v)
            print(list_one[numpy.random.choice(len(list_one), p = list_two)])
            self.queue = [s for s in c.seedURLs]
            self.fetched = []
        url = self.queue[-1]
        self.queue.pop(-1)
        self.fetched.append(url)
        return url

    def updateURLs(self, c, newURLs, newURLsWD, iteration):
        tmpList = [url for url in newURLs]
        tmpList.sort(key = lambda url: url[len(url) - url[::-1].index('/'):])
        for url in tmpList:
            self.queue.append(url)

class LIFO_Cycle_Policy:
    def __init__(self, c):
        self.queue = [s for s in c.seedURLs]
        self.fetched = []

    def getURL(self, c, iteration):
        while True:
            if (len(self.queue) == 0):
                break
            elif self.queue[-1] in self.fetched:
                self.queue.pop(-1)
            else:
                break

        if (len(self.queue) == 0):
            #return None
            self.queue = [s for s in c.seedURLs]
            self.fetched = []
        url = self.queue[-1]
        self.queue.pop(-1)
        self.fetched.append(url)
        return url

    def updateURLs(self, c, newURLs, newURLsWD, iteration):
        tmpList = [url for url in newURLs]
        tmpList.sort(key = lambda url: url[len(url) - url[::-1].index('/'):])
        for url in tmpList:
            self.queue.append(url)

class LIFO_Policy:
    def __init__(self, c):
        self.queue = [s for s in c.seedURLs]

    def getURL(self, c, iteration):
        if (len(self.queue) == 0):
            #return None
            self.queue = [s for s in c.seedURLs]
        url = self.queue[-1]
        self.queue.pop(-1)
        return url

    def updateURLs(self, c, newURLs, newURLsWD, iteration):
        tmpList = [url for url in newURLs]
        tmpList.sort(key = lambda url: url[len(url) - url[::-1].index('/'):])
        for url in tmpList:
            self.queue.append(url)

# Parser class
class Parser(HTMLParser):
    def __init__(self):
        HTMLParser.__init__(self)
        self.output_list = []
    def handle_starttag(self, tag, attrs):
        if tag == 'a':
            self.output_list.append(dict(attrs).get('href'))

### generatePolicy classes

# Dummy fetch policy. Returns first element. Does nothing ;)
class Dummy_Policy:
    def getURL(self, c, iteration):
        if len(c.URLs) == 0:
            return None
        else:
            return c.seedURLs[0]

    def updateURLs(self, c, newURLs, newURLsWD, iteration):
        pass

#-------------------------------------------------------------------------
# Data container
class Container:
    def __init__(self):
        # The name of the crawler"
        self.crawlerName = "IRbot"
        # Example ID
        self.example = "exercise3"
        # Root (host) page
        self.rootPage = "http://www.cs.put.poznan.pl/mtomczyk/ir/lab1/" + self.example
        # Initial links to visit
        self.seedURLs = ["http://www.cs.put.poznan.pl/mtomczyk/ir/lab1/"
            + self.example + "/s0.html"]
        # Maintained URLs
        self.URLs = set([]) #  Set of all discovered URLs (strings)
        # Outgoing URLs (from -> list of outgoing links)
        self.outgoingURLs = {}
         # Incoming URLs (to <- from; set of incoming links)
        self.incomingURLs = {}
        # Class which maintains a queue of urls to visit.
        #self.generatePolicy = Dummy_Policy()
        #self.generatePolicy = LIFO_Policy(self)
        #self.generatePolicy = FIFO_Policy(self)
        #self.generatePolicy = LIFO_Cycle_Policy(self)
        self.generatePolicy = LIFO_Authority_Policy(self)
        # Page (URL) to be fetched next
        self.toFetch = None
        # Number of iterations of a crawler.
        self.iterations = 10

        # If true: store all crawled html pages in the provided directory.
        self.storePages = True
        self.storedPagesPath = "./" + self.example + "/pages/"
        # If true: store all discovered URLs (string) in the provided directory
        self.storeURLs = True
        self.storedURLsPath = "/" + self.example +"/urls/"
        # If true: store all discovered links (dictionary of sets: from->set to),
        # for web topology analysis, in the provided directory
        self.storeOutgoingURLs = True
        self.storedOutgoingURLs = "/" + self.example + "/outgoing/"
        # Analogously to outgoing
        self.storeIncomingURLs = True
        self.storedIncomingURLs = "/" + self.example + "/incoming/"


        # If True: debug
        self.debug = True

def main():

    # Initialise data
    c = Container()
    # Inject: parse seed links into the base of maintained URLs
    inject(c)

    # Iterate...
    for iteration in range(c.iterations):

        if c.debug:
            print("=====================================================")
            print("Iteration = " + str(iteration + 1) )
            print("=====================================================")
        # Prepare a next page to be fetched
        # The page to be fetched is selected. The method generate invokes object
        # under generatePolicy which selects one URL and saves it under toFetch
        # variable.
        generate(c, iteration)
        if (c.toFetch == None):
            if c.debug:
                print("   No page to fetch!")
            continue

        # Generate: it downloads html page under "toFetch URL"
        page = fetch(c) # Please note that the name of the crawler is added to the request

        if page == None:
            if c.debug:
                print("   Unexpected error; skipping this page")
            removeWrongURL(c)
            continue

        # Parse file
        htmlData, newURLs = parse(c, page, iteration)

        # The method saves downloaded HTML page under the provided directory.
        # The page is saved each time it is visited because it may have changed since
        # the last retrieval.

        # Store pages
        if c.storePages:
            storePage(c, htmlData)

        ### normalise newURLs
        newURLs = getNormalisedURLs(newURLs)

        ### update outgoing/incoming links
        updateOutgoingURLs(c, newURLs)
        updateIncomingURLs(c, newURLs)

        ### Filter out some URLs
        newURLs = getFilteredURLs(c, newURLs)
        # ^Default implementation removes all URLs
        # that are not a subdirectory of the root path.
        if not newURLs:
            continue

        ### removeDuplicates
        newURLsWD = removeDuplicates(c, newURLs)

        ### update urls
        c.generatePolicy.updateURLs(c, newURLs, newURLsWD, iteration)
        # Then, updateURLs method of fetchPolicy is invoked. All extracted links
        # newURLs (normalised and filtered out) are passed as well as
        # newURLsWD (newURLs after removal of duplicated links). This method
        # should be used to inform generatePolicy about newly detected links.

        # Add newly obtained URLs to the container
        if c.debug:
            print("   Maintained URLs...")
            for url in c.URLs:
                print("      " + str(url))

        if c.debug:
            print("   Newly obtained URLs (duplicates with maintaines URLs possible) ...")
            for url in newURLs:
                    print("      " + str(url))
        if c.debug:
            print("   Newly obtained URLs (without duplicates) ...")
            for url in newURLsWD:
                    print("      " + str(url))
            for url in newURLsWD:
                c.URLs.add(url)

    # store urls
    if c.storeURLs:
        storeURLs(c)
    if c.storeOutgoingURLs:
        storeOutgoingURLs(c)
    if c.storeIncomingURLs:
        storeIncomingURLs(c)


#-------------------------------------------------------------------------
# Inject seed URL into a queue (DONE)
def inject(c):
    for l in c.seedURLs:
        if c.debug:
            print("Injecting " + str(l))
        c.URLs.add(l)

#-------------------------------------------------------------------------
# Produce next URL to be fetched (DONE)
def generate(c, iteration):
    url = c.generatePolicy.getURL(c, iteration)
    if url == None:
        if c.debug:
            print("   Fetch: error")
        c.toFetch = None
        return None
    # WITH NO DEBUG!
    print("   Next page to be fetched = " + str(url))
    c.toFetch = url


#-------------------------------------------------------------------------
# Generate (download html) page (DONE)
def fetch(c):
    URL = c.toFetch
    if c.debug:
        print("   Downloading " + str(URL))
    try:
        opener = req.build_opener()
        opener.addheadders = [('User-Agent', c.crawlerName)]
        webPage = opener.open(URL)
        return webPage
    except:
        return None

#-------------------------------------------------------------------------
# Remove wrong URL (TODO)
def removeWrongURL(c): # solved
    if c.toFetch in c.URLs:
        c.URLs.remove(c.toFetch)

#-------------------------------------------------------------------------
# Parse this page and retrieve text (whole page) and URLs (TODO)
def parse(c, page, iteration):
    # data to be saved (DONE)
    htmlData = page.read()
    # obtained URLs (TODO) solved
    p = Parser()
    p.feed(str(htmlData))

    newURLs = set([s for s in p.output_list])
    if c.debug:
        print("   Extracted " + str(len(newURLs)) + " links")

    return htmlData, newURLs

#-------------------------------------------------------------------------
# Normalise newly obtained links (TODO)
def getNormalisedURLs(newURLs): # solved
    nURLs = set([url.lower() for url in newURLs])
    return nURLs

#-------------------------------------------------------------------------
# Remove duplicates (duplicates) (TODO)
def removeDuplicates(c, newURLs): # solved
    toLeft = set([url for url in newURLs if url not in c.URLs])
    if c.debug:
        print("     Removed " + str(len(newURLs) - len(toLeft)) + " urls")
    return toLeft # Fix in ex1e and 2a

#-------------------------------------------------------------------------
# Filter out some URLs (TODO)
def getFilteredURLs(c, newURLs): # solved
    toLeft = set([url for url in newURLs if url.lower().startswith(c.rootPage)])
    if c.debug:
        print("   Filtered out " + str(len(newURLs) - len(toLeft)) + " urls")

    # To eleminate trap
    toLeftNEW = set([url for url in toLeft if url not in c.toFetch]) # Fix in ex1e and 2a

    return toLeftNEW

#-------------------------------------------------------------------------
# Store HTML pages (DONE)
def storePage(c, htmlData):

    relBeginIndex = len(c.rootPage)
    totalPath = "./" + c.example + "/pages/" + c.toFetch[relBeginIndex + 1:]

    if c.debug:
        print("   Saving HTML page " + totalPath + "...")

    totalDir = os.path.dirname(totalPath)

    if not os.path.exists(totalDir):
        os.makedirs(totalDir)

    with open(totalPath, "wb+") as f:
        f.write(htmlData)
        f.close()

#-------------------------------------------------------------------------
# Store URLs (DONE)
def storeURLs(c):
    relBeginIndex = len(c.rootPage)
    totalPath = "./" + c.example + "/urls/urls.txt"

    if c.debug:
        print("Saving URLs " + totalPath + "...")

    totalDir = os.path.dirname(totalPath)

    if not os.path.exists(totalDir):
        os.makedirs(totalDir)

    data = [url for url in c.URLs]
    data.sort()

    with open(totalPath, "w+") as f:
        for line in data:
            f.write(line + "\n")
        f.close()


#-------------------------------------------------------------------------
# Update outgoing links (DONE)
def updateOutgoingURLs(c, newURLsWD):
    if c.toFetch not in c.outgoingURLs:
        c.outgoingURLs[c.toFetch] = set([])
    for url in newURLsWD:
        c.outgoingURLs[c.toFetch].add(url)

#-------------------------------------------------------------------------
# Update incoming links (DONE)
def updateIncomingURLs(c, newURLsWD):
    for url in newURLsWD:
        if url not in c.incomingURLs:
            c.incomingURLs[url] = set([])
        c.incomingURLs[url].add(c.toFetch)

#-------------------------------------------------------------------------
# Store outgoing URLs (DONE)
def storeOutgoingURLs(c):
    relBeginIndex = len(c.rootPage)
    totalPath = "./" + c.example + "/outgoing_urls/outgoing_urls.txt"

    if c.debug:
        print("Saving URLs " + totalPath + "...")

    totalDir = os.path.dirname(totalPath)

    if not os.path.exists(totalDir):
        os.makedirs(totalDir)

    data = [url for url in c.outgoingURLs]
    data.sort()

    with open(totalPath, "w+") as f:
        for line in data:
            s = list(c.outgoingURLs[line])
            s.sort()
            for l in s:
                f.write(line + " " + l + "\n")
        f.close()


#-------------------------------------------------------------------------
# Store incoming URLs (DONE)
def storeIncomingURLs(c):
    relBeginIndex = len(c.rootPage)
    totalPath = "./" + c.example + "/incoming_urls/incoming_urls.txt"

    if c.debug:
        print("Saving URLs " + totalPath + "...")

    totalDir = os.path.dirname(totalPath)

    if not os.path.exists(totalDir):
        os.makedirs(totalDir)

    data = [url for url in c.incomingURLs]
    data.sort()

    with open(totalPath, "w+") as f:
        for line in data:
            s = list(c.incomingURLs[line])
            s.sort()
            for l in s:
                f.write(line + " " + l + "\n")
        f.close()



if __name__ == "__main__":
    main()
