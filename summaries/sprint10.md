# Week 9

## Yago

My two main goals for this sprint were getting the calendar export ready to merge, and enhancing the UI for handling due dates.

The calendar export PR ([#245](https://github.com/steroid-team/app/pull/245)) got some merging conflicts that were relatively easy to solve, but led to a hard to debug testing problem that took more time than expected. Fortunately, it has been fixed and we were able to finally merge the PR.

I was also able to create the code for nicely displaying due dates, but the tests are not behaving as expected.

As usual, I gave architecture guidance to my teammates and provided support with the daily GitHub grooming, but I was only able to review [#265](https://github.com/steroid-team/app/pull/265), [#266](https://github.com/steroid-team/app/pull/266) and [#247](https://github.com/steroid-team/app/pull/247).

Overall the sprint was okay, but my other academic duties kept me from making out of this sprint a really good one. Next week I’ll try to step up my game and spend some extra time reviewing as well.


## Leandro

This week I’ve worked on adding persistence to the tags. 
It was pretty difficult because I had to modify a lot of files, which resulted in a big PR. I had several issues among the way, but the rest of the team helped me fix them. 
I also enhanced the design of the views for the tags. 
I should probably have separated these two features into two distinct branches, but I thought about it too late. 
I will try to be more careful with feature separation next time. Also it was a very tough week, I was overwhelmed.


## Noah

This week I’ve worked on the adapter from the todolist to be able to group the task by date. 
It was more or less hard but I asked Theo for help since now he’s the RecyclerView/MVVM’s god and he helps me a lot to understand how I had to do things. 
I’ve also reviewed the PR for the cache and others. 
Anyway, I still have to work earlier on my task, because I still have issues with conflicting PR and tests late during the sprint. 
It would be more manageable to discover this kind of thing early in the sprint.


## Théo (scrum master)

This week, I finished and we merged the local cache.
I also work on the header persistence but I need to solve the new conflicts. 
I spent a lot of time trying to fix the test and have rounded corners for the image header in a note that I couldn't work on my other task.
I reviewed as many PRs as possible.
This week for me was not terrible as we merged my PR about caching. But for sure note the best one.


## Vincent

This week I had the PR about done tasks deletion merged, and worked on the daily briefing feature. I also reviewed #242 which is quite an important PR. I also got back to struggling with tests.

For me this week was very similar to last week, but with the additional difficulty of the tests which took more of my time.


## Sydney

I addressed issue #227, and reviewed some PRs. Nothing special to mention this week.

## Overall team

This week we still have many troubles with the tests. I think everyone got frustrated because we spend so much time on SDP to just get stuck on the test and trying to have Cirrus working fine.
Even if this week the project didn't go well (PRs got merged late before Friday), I think that the whole team made a lot of effort and everyone tried to help each other.

Otherwise, we merged the last requirement needed for the application. And it was the last big PR with some refactoring.
For the next week, very cool features are coming and it will be easier as we are done with all the big architecture stuff.
