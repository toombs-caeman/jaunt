![jist icon](app/src/debug/res/mipmap-xhdpi/ic_launcher_round.png)

# jist
a minimum viable note app

# why
Most note apps are overkill for keeping a handy grocery list.
I'm not writing extensively on my phone, so anything beyond the most basic features just slows down the most basic use case.

The core idea is to take quick notes quickly by reducing overhead and options.
* There is no save button; Notes are saved automatically.
* There is no create button; Searching for a note that doesn't exist will create it.
* There is no delete button; Empty notes are automatically deleted.
* The app always opens on the last edited note and you can start typing immediately.

The following "features" will never be added:
* tags, groups, favorites - if you really need this you probably have a lot (100s) of notes. This is not our use case.
* markup, markdown, markleft, markunder, formatting of any kind - it's just text y'all. Formatting options would clutter the interface.
* multiple note types - this complicates the create/save step. Options require decisions.
* ads - ads are lame
* remote backup/sync - I'm lazy, also for privacy

# potential future features
* full text search, fuzzy search
* note list sort order options (most recently used, best match or alphabetical). Currently alphabetical
* history: option to discard changes instead of saving, more generally note edit history
* import/export notes as .zip or .json

Adding some of these would create the need for an overflow or 'kebab' menu, which is likely to be the only UI change ever.

# the name
* just (adverb) - only, precisely
* gist (noun) - the main point or part, essence

Hence: jist, an app for just the gist
