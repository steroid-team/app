# Week 5

## Yago

My main goal for this sprint was to implement the drawer/sidebar, which required way more work than expected (including an important restructure of all our views to use the Fragment Android pattern). Because of the fundamental nature of the changes, almost all our tests must be adapted, which has quickly gotten out of the scope of the sprint.

Because of this, unfortunately, I have not been able to submit a PR nor work on my second task.

In hindsight, I think I could only have avoided this by having more experience with Android. Still, I will try to be better informed before making time estimates for my tasks. It will be very exciting to have the sidebar merged next week!

Regardless of this hiccup, I tried to keep up with the collaboration & reviews. I reviewed #80, #146, #151 and #119, as also helped Théo along the development of the latter.

## Leandro


## Noah

This week I’ve finished the searchBar for the GPS and corrected the current user position, but I wasn’t able to get my PR merged as I had a lot of troubles with the tests and Cirrus… I have also reviewed some PRs and tried to give accurate feedback. I had to learn a lot about asynchrony and it will be surely useful for the other sprints. Anyway, I still have to fix the tests for the GPS part and it will be a great feature to use !

I have managed my time pretty well but I have spent way more than 8 hours this week for SDP so maybe I need to better define small tasks.

## Théo

This week, I finished my work on the implementation of the logic of the todo-lists and tasks. I had many issues with the recycler view that we use when we scroll down so I had to make some changes in the layouts to modify tasks. I also ran into many problems testing my code. All the tests were successful but again on the CI, it resulted in many errors. Yago helped me to display more information about the run so it was easier for me to find the bug.
I also did some designs for the work I coded. I tried to review as much as possible the PRs.

It wasn't my best week as I spent too much time on my issues. And also the little design I did take more time than I expected.

So for the next week, I will try to be more careful with the testing part and go step by step to spot easily issues on the CI.

## Vincent (scrum master)

This week I worked on integrating images by allowing to add a main image to a note which will be displayed in the header of the note. It took much longer than expected to make it work so I couldn’t work on integrating the Firebase database with this feature.
I also spent some time reviewing/checking PRs, which were quite large this week. All in all I think I spent quite a lot more time than the 8 hours weekly SDP but that compensates for previous week.

Next week I will try more or less to continue like this.

## Sydney

This week I addressed issues #97 and #98 on remote database persistency. Quite a lot of refactoring was involved since the database interface we’ve written so far wasn’t meant to deal correctly with asynchrony. This issue was already discussed and meant to be addressed in the previous sprints but it wasn’t finalized when I needed it. Therefore I decided to address asynchrony myself in order to effectively work on my tasks. Some issues were encountered : merge conflicts on the database part of the code and adapting tests related to the database and making tests pass in the CI.

Unfortunately I wasn’t able to find time to actually code and setup a PR for issue #140 on voice recording memos.

Lastly, I intend to review some PRs but this will be done after this week’s summary that is submitted on Thursday evening.


## Overall team

This week there has been a push to add sensor usage and working with files in the app, which are features long to implement and that can be quite hard to test.

In general the pull requests for this sprint were quite large which makes it harder to review and to merge. I don’t expect it to remain an issue for next weeks. We still managed to merge a few but we left a backlog for next week.

Additionally, this week the time estimates were too low in general. In particular, the testing part of the work has been underestimated I believe.

