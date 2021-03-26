# Week 3

## Yago (scrum master)

I have been working on the visual identity of the app, namely choosing a name
for the app, designing a logo
([#84](https://github.com/steroid-team/app/issues/84)) and preparing some
graphic guidelines that I later applied to create an enhanced version of our
app mockups (see [#55](https://github.com/steroid-team/app/issues/55)).

I was also responsible for some housekeeping: fixing our linter warnings
([#79](https://github.com/steroid-team/app/issues/79))
and adding an extra layer to our testing/CI suite to make sure that we keep a
more uniform style across the codebase.

Unfortunately, the chores + the logo design took a bit longer than expected, so
I could not implement the task modification feature.

It was a productive week, but I have realized the importance of knowing a topic
to do good estimates (I was not familiar with code stylers nor design). Next
time I will add some extra margin time for those tasks that are especially out
of my comfort zone, to be ready for the unexpected.

## Leandro

This week I did the PR for the controller for the list selection activity. I
didn’t spend too much on the tests since it is probably going to be
modified/deleted when switching to MVVM. I also  implemented the button to
create a new list on the list selection screen. I am not fully satisfied with
my work, I feel like I could have been more efficient this week. For the next
sprint I will try to get organized in a better way, so that I can allocate more
time to the project. Also I will try to get more efficient in the way I work.

## Noah

This week I have search documentation about Toast  and alert messages and also
Intent to transfer something in a View to another View. With that I was able to
make a renaming feature for the todolists. I also have worked based on the MVVM
pattern and the LiveData work that Theo has done to make the call to the
database asynchronous so that the application doesn’t freeze if it takes time
to get a result. I have also reviewed PR #49 and #76. 

I have made progress on working earlier in the week but I definitely have to
improve my organisation overall. I think I have to ask more questions if I
don’t understand something and not try to investigate myself every time because
it is quite time consuming.

## Théo

This week, I did some research about the MVVM pattern and LiveData. I tried to
implement the MVVM pattern for the Item View activity. And so I integrated the
view, the model, and the volatile database for now, together.

I only did this but for me it was big and it took me a lot of time because it
was new for me (the LiveData package and I was mostly used to the MVC pattern).

I am quite satisfied with the result because all the things work well together,
it was a big refactor. But now we can just add new features with good
architecture.

## Vincent

This week I implemented the note feature, both the views and the database. It
works well but I didn’t commit too much to make it perfectly clean, as a
refactoring is coming.

I am satisfied on my personal work, however this week I didn’t have much time
to perform reviewing of other PRs. I’ll try to do more of that next week.

## Sydney

Implemented issue #67 on user concept in the app. The implementation itself
wasn’t that difficult to do. However testing proved difficult to implement
because the implementation of the user concept introduced an external
dependency to the firebase authentication service. I had to take time to learn
Mockito in order to test and mock.

Issue #57 on making CRUD actions on todolists persistent in the database
required more work than expected. Some operations needed on the database were
missing and needed to be addressed. Unfortunately I realized it too late at the
end of the sprint.

Otherwise I reviewed some PRs in the meantime.

In conclusion, I should next time address all the tasks at the very start of a
sprint instead of working on one task after the other and end up working on the
last one the day before the next weekly meeting. Or, we should focus on only
one task per sprint in the future.

## Overall team

We have done really well this week: we started with a barely functional app,
and we’re closing the sprint with something that is very close to a minimum
viable product.

Our bet has been that some early investments in good code quality will help us
go much faster in the following weeks, and hopefully we will be ready to start
reaping the benefits as soon as next week.

We are getting better at time estimates but we are falling a bit behind with
the testing (we are still finishing the sprints with some unmerged PRs due to
broken builds). However, our the overall “software throughput” is increasing
drastically, which is awesome news!
