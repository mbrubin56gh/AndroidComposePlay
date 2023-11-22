# SampleAndroidTakeHome

This is an implementation of a standard "take home" Android project for job interviews: it queries for a list of robots from https://dummyjson.com/,
displays them in a list and supports swipe to refresh. There's an error screen if the initial network fetch fails, and a detail screen if you click
on an item in the list. Nothing very fancy -- just demoing how this can all work. It also persists fetched results to a db and uses that as the 
source of truth (so supporting being offline). It's really just the basics, but implemented with Compose, coroutines, Flow, Room, Dagger + Anvil, 
and Circuit. There are Circuit unit tests, but no UI tests or snapshot tests (because I'm feeling lazy about all that).

I didn't do a very good job keeping my commits clean and of professional quality. You can get some sense of the progression from them, but
don't pay too strict attention there.

There's a Compose Navigation variant that's a bit less refined over in the compose-navigation branch.