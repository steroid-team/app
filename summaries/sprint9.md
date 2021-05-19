# Week 9

## Yago (scrum master)

My two main tasks for this sprint were implementing the UI for adding due dates
to tasks, and exporting tasks to an external calendar.

The former was a relatively big task, so following my goals from previous
sprints I split it into two smaller chunks: actual setting of due dates, and
displaying them.

For the setting, I implemented a natural language processing system that
automatically recognises the dates and times within a note’s body, and parses
it as the due date (see PR
[#236](https://github.com/steroid-team/app/issues/239)). Displaying is still in
progress.

The calendar export for tasks has also been implemented in PR
[#245](https://github.com/steroid-team/app/issues/245). However, there seems to
be a rather obscure issue with the Google Calendar app in Cirrus’s emulator,
which has kept it from becoming production-ready.

Besides this, the team was struggling with the flakiness of our test suite, so
I did the following to address it: Opened
[#231](https://github.com/steroid-team/app/issues/231) as a meta-issue to track
these random flakes.  Submitted PR
[#236](https://github.com/steroid-team/app/issues/236), which after discussions
in our last team meeting has had quite a good impact on the randomness issues.

Furthermore, I found a bug and reported it as issue
[#237](https://github.com/steroid-team/app/issues/237), created
[#249](https://github.com/steroid-team/app/issues/249), reviewed
[#247](https://github.com/steroid-team/app/issues/247) and
[#217](https://github.com/steroid-team/app/issues/217).

I am satisfied with my work this week: almost all of it was submitted with a
lot of time before the sprint’s deadline, and it has been very productive
overall. Considering that this also happened last sprint, I might be getting on
the right track!

## Leandro

This week I worked again on fitting a note in a drawing because of issues I
didn’t encounter before. Théo also helped me on that task. This work was pretty
frustrating because it took me a lot of time (nearly eight hours) and I still
couldn’t get the wanted result. Therefore, I decided to simplify the feature,
but make sure what is implemented works. I also worked on the tag concept, and
the view to display and create tags. The next step will be making the view to
modify them, and making them persistent. I also reviewed PR #236. I feel like I
could manage my time better this week. I worked more than eight hours but not
enough on reviewing PRs as I intended.

## Noah

This week I’ve finished the PR for the reminder notification based on the
location and I’ve also made another PR to connect the due date part of the task
with the notification based on the due date to make everything work together !
I’ve struggled a lot with the tests this week, because I had to mock the
location of the emulator and stuff like this. I have worked way more than these
8 hours because of these tests. I have also reviewed some PR’s from everyone. 
I think we’ve made really nice improvements about the organization. But for next week I will try to work earlier on my task and maybe less focus on the test and asks for help when needed, it could save me a lot of time.

## Théo

This week I succeeded to merge my PR about note management.

I worked on caching the data so that the app is more enjoyable for the user. So
the app also has the same functionality if you are offline. But you still need
to log in one time, and after if you lose connection there is no problem. I
also needed to refactor the ViewModel and repository to use the local file
system I implemented. I decided to create a FileStorageDatabase, a database
that takes two file system services, a remote and local one, and this database
is responsible also for the caching management. I also tried to test as much as
possible and I achieved 88% of total coverage. But on Thursday we merged a PR
about voice memo persistence that does some changes in the database, so I
couldn't fix the merging conflict and implement the new functionalities in the
local file system. So I wasn't able to have this PR ready for Friday.
This took me a lot of time so I wasn't able to work on my PR about image persistence.

I reviewed PRs: 218, 239, 246, 238, and 217.

Even if I couldn't merge all my PRs this week, I am happy about the work I did,
and I think with the cache feature it will be easy to have a fully functional
application without an internet connection, as it is already if you lost the
connection.

## Vincent

This week I finished the feature to be able to integrate camera photos in the
app, and I advanced the task to have a button removing all tasks that are done.
I also reviewed some PRs.

For me this week went pretty well overall although I struggled a bit with the
Android ecosystem as I often do.

## Sydney

This week was not the most productive one in terms of hours spent, but I
managed to merge two PRs. 
I managed to solve test issues with last sprint’s PR #246 which allowed me to merge it.
Second, I spent some time cleaning up most of the code’s lint issues we
accumulated so far.
Lastly, I spent some time reviewing other’s PRs and trying to solve issues
preventing them from being merged.

## Overall team

Our focus this week was set on every single member of the team having an item
marked as “Done” by the end of the sprint, and I am super excited to say that
we have achieved it! :tada:

The CI issue we had at the beginning of the week was very cumbersome for the
whole team, so we prioritised it accordingly and we managed to alleviate its
effects early on.

The whole team has really made a big effort to keep tasks small and manageable,
and after the good results we have obtained, we will definitely keep doing it.

However, we have fallen a bit short in keeping our currently open pull requests
up to date, so there are a couple of them that are not merged yet.

We also have a few draft PRs _in the oven_, which means that next sprint will
probably be quite active. We will try to prioritise reviews to keep the PR
backlog under check!
