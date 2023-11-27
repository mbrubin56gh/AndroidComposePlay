# AndroidPlayground

A little Android app playground for playing with, chiefly:
- Compose
- Compose Navigation
- Room
- Dagger + Anvil 
- Coroutines + Flow

This is an implementation of a simple Android project that fetches a list of items (robots from https://dummyjson.com/) and supports clicking
on a list item to navigate to a detail screen. The list screen supports swipe to refresh. There's an error screen if the initial network 
fetch fails (which also supports swipe to refresh). Nothing very fancy -- just demoing how this can all work. It also persists fetched results
to a Room db and uses that as the source of truth. It's really just the basics, but implemented with Compose, coroutines, Flow, Room, Dagger + Anvil, 
and Compose Navigation. There are no unit tests, UI tests, or snapshot tests, because I'm feeling lazy about all that, and because I like the 
Circuit version better, and I added unit tests there.

I didn't do a very good job keeping my commits clean and of professional quality. You can get some sense of the progression from them, but
don't pay too strict attention there.

There's a circuit version of this app in the circuit branch.