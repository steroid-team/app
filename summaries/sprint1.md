# Summary for week 1

## Yago

I discussed with Sydney how we should implement the database in an extensible
way, such that we can add the expected features in the future while keeping a
good performance. After settling on using Firebase, I have begun implementing
the DB service and reviewed [#11](https://github.com/steroid-team/app/pull/11)
and [#15](https://github.com/steroid-team/app/pull/15).

Unfortunately, the initial research stage and our general architecture
discussions took a lot of time before the implementation, so I have not been
able to fully complete the service.

Next time, I will try to:

- Take on more bite-sized tasks that do not depend on others.
- Make sure that they can be accomplished within a week.
- Profit from instant messaging to avoid being blocked before having a meeting.

## Leandro

I drew the first mockup of the main activities that we will have. I transposed
these drawings on the computer to have cleaner and nicer images.  The final
design may be different from the mockup ; its goal is mostly to provide a
structure for the views. 

Next time, I will try to implement the layout of the list selection screen and
get involved in a more complex task.

## Noah

I discussed with Theo about the structure to really understand the relationship
between the classes. I have implemented a small controller so that the view can
interact with the database.

Next time, I will try to manage my time better and make better estimation of
the duration of a task. 

## Théo

This week, I tried to implement a basic and volatile database. I also implement
a simple class Task to start managing those tasks. So we can agree on this
interface and later replace the volatile database with one using Firebase.
Besides teammates, who start creating the View of the app, get some methods to
work with the task.

For me, the big problem this week was the fact that we have mostly dependent
tasks between each other before our first stand-up meeting. 

Next week, I will try to:

- Implement basic logic and class for to-do lists.
- Think more about how I can start coding directly by doing modular class…

## Vincent

I implemented the TODO task view from the design mockup Leandro drew as an
Android activity. In order to do it I had to read the appropriate Android
documentation/tutorials which were longer than expected.

Next time, I will try to manage my time better and be more careful about the
dependencies of tasks of the team (especially since the project is started from
scratch).

## Sydney

Yago and I discussed about cloud services, online data storage and data
representation. We decided that for simplicity we will make use of Firebase’s
services for user authentication, user data storage and file data storage.

Afterwards it has been decided that I take care of implementing a simple
Android Activity that enables a user to authenticate through different means
with the help of Firebase’s authentication service. This has been implemented
without much difficulty.

Problems encountered where the research stage around cloud services/backend
solutions and the lack of a clear view of what to do and what was possible to
do. Between the last sprint meeting and the daily standing meeting this part
was still not clear to us. After discussing with Yago before the daily stand up
we were able to have a better grasp on concrete tasks we could achieve.
Finally, this first sprint showed how difficult it is to plan ahead and work in
parallel without depending on each other. Hopefully it will be much easier in
the future to work on the app when we will have a strong code base to work on.

Next time I will spend more time on architectural aspects of the app so that we
could more easily split up tasks among us.

## Overall team

We did not fully implement the user story for this spring, but we think we did
a very good start. The unfinished tasks remain at top priority for the next
spring.

A lot of time went into research and coordinating with each other, however,
this allowed us to set up a good organisation and communication that will
certainly be very useful for the future.

Because of our various plans and schedules we could only do one standup meeting
this week, but we took measures to all be available for a second one.
