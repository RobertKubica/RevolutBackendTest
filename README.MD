Classes:

**Main** - running http server

**ServerTest** - automated test to run server and send example requests

**ConcurrencyTest** - shows that overlapping transfers doesn't cause deadlock

Things to consider:
* AccountView shouldn't be interface implemented by Account, because it would be easily accessible via reflection, but I left it that way for sake of simplicity