# Week 8
## Yago
In this sprint I finished the rich editor for notes, which allows a wide range of formatting as well as embedding images. It took some extra time to switch the initial work (based on Activities) to Fragments, and I ran into some unrelated testing issues that I solved to reduce the flakiness of our test suite. See PR [#191](https://github.com/steroid-team/app/pull/191).

This week we were also starting the work on notifications. In preparation for that, I modified our model of Tasks to hold the required information. During the process I took the opportunity to make our database API more consistent with the CRUD model, allowing us to reduce code redundancy and making it easier to implement future features. See PR [#212](https://github.com/steroid-team/app/pull/212).

I was as well supposed to create a view to set the due date, but I realised that it would be better to _automagically_ identify the dates from the task’s body rather than having a normal “date picker”. I have already made some progress on it, but that makes the task more complicated than originally planned, so it will still take some time to complete next week.

During the sprint I also reviewed [#209](https://github.com/steroid-team/app/issues/209) (1st & 2nd reviews), and [#119](https://github.com/steroid-team/app/issues/119) (2nd & 3rd reviews).

I am quite satisfied with the progress this week: I submitted my PRs early, they got merged quite easily, and all the work was very aligned with the project’s goals. Since it’s been one of my best sprints so far, I will try to repeat it in the following ones.
## Leandro
This week I made the drawing fittable in a note, just as an image. My work was made very easy thanks to Yago’s previous PR about embedding images. I got stuck at some point, but I found the solution thanks to Théo and Sydney’s help. I also worked on the tags for the todo lists ; I think I am halfway through it at the moment. This week I worked slightly less than eight hours and pretty late of the week, mainly because of other projects and personal impediments. Next week I will try to dedicate more time to SDP and to review more PRs.
##  Noah
This week I have merged my PR for the VoiceRecordActivity and the one for the Notification date, I have also reviewed some PR’s. It was a productive week and I think that I’ve improved at managing my time better, I start to work on my task earlier than the other sprints. But I still have trouble to estimate the time of a task since I thought that doing the notification part wasn’t so difficult which was correct but I had a lot of trouble with getting it working on our current version of android and also with the test. Next week, I’ll still work on the location reminder notification task since I’ve already started working on it but couldn’t make it work for now.
##  Théo
This week I finished the list selection fragment. I did some refactoring so I can re-use my work in other PR and we have a cleaner architecture. This took much time than expected but I am happy with the result.
So my work on the note selection was much easier. I implemented basic functionalities (renaming, creation and deletion). Some already existed but I needed to refactor them with the mvvm pattern and the firebase database. I also took time to get a nice design. Even if I got strange behavior of the CI with dialog fragment. I also did a PR about note persistency.
Even if leandro work on the integration of the drawing in the note, our tasks were well splited and we didn't overlap.
I think this week was quite nice for me. I managed to merge the PR about ListSelection early on the week. That was expected as the previous week the team decided that the PR of Yago and Sydney were proprietary. I also succeeded to split my work in many tasks so I had more PRs that were ready before thursday.

##  Vincent
This week I worked on allowing the user to include a photo using a camera to include in the note. I encountered a few small issues while developing the feature but I solved them in reasonable time and in the end I am happy of the work. However, much like with the file picker feature, I am still struggling in finding a way to test it without using deprecated methods. So I made a draft PR for it as it is not ready for reviewing yet.

Also, I didn’t manage to find time to review other PRs. I’ll try to focus more on that next week.
##  Sydney (scrum master)
First, I was able to address only one of my two tasks assigned this week. Issue #154 about persisting voice memos took more time than expected so I decided to drop the other issue for the week after as the other one was not high priority.
Second, I reviewed and merged some PRs during the week.
Finally, I helped Leandro while he was dealing with an issue in his current week’s sprint task.

This week was not perfect but far better than the previous weeks. This time I was able to complete a task during the same sprint because of better anticipation.

## Overall team
As a scrum master, I can say that the team works smoothly, communicates well and that we all get along.

As usual, many factors contribute to longer hours on the project : failing test pipeline, lack of anticipation, too big tasks etc.

But contrary to previous weeks, and even though there is still room for improvement, I can feel that we are more efficient with regard to planning, anticipation, working experience and so on…
