# Week 6

## Yago

As promised, the sidebar is finally live! :tada: [#170](https://github.com/steroid-team/app/issues/170)

Rebasing with the latest master was a bit of a nightmare, because it extended the scope of the Activity-to-Fragment migrations, as well as created conflicts with the pre-existing tests.

The main lesson we get from this is how crucial it is to split tasks into smaller ones that can also be translated into atomic PRs. It will be one of my top priorities from now on, especially before I start coding.

In terms of team collaboration, I would like to thank Théo for guiding me through our use of the MVVM paradigm to understand it a little better. I myself opened issue [#171](https://github.com/steroid-team/app/issues/171), reviewed [#150](https://github.com/steroid-team/app/issues/150) and gave my 2nd review on [#80](https://github.com/steroid-team/app/issues/80).


## Leandro

This week I kept working on the drawing activity, especially on the testing part. However, I stumbled on some problems, which took me a lot of time and kept me from getting the wanted coverage. I also worked on the color picker, which now works very well. I found a library that I think fits really well with our design. It took me a little time to understand how to import and use it, but I am happy with the result. I still have to write more tests for it to be fine. Since the beginning of the project writing the tests is probably what takes me the most time and gives me the hardest time. Once again, I couldn’t finish my last task because of that, although I spent way more than eight hours. Next week I will try to do some research to write my remaining tests, and I will try to ask for help more often if I get stuck.
Also, I reviewed PRs #80 and #121.

## Noah (scrum master)

This week I have finally finished the GPS part of the app and it works well ! I have finally well understood how the searchView works and how to test it. I have also made reviews for the Drawing #164 and #149, but the most time consuming part of this sprint for me was how to add tests for the mapsActivity. We merged it even if it dropped a bit the total coverage to 73% because we will easily grow that up with some more tests on other parts !

I’m happy that this week I have managed to start working early in the week so I won’t have to do everything on Wednesday and Thursday. 

Next week I’ll try to implement a new cool feature for the app !

## Théo

This week, I did some improvements/designs on the list selection view. I also did my work for the note-activity (convert to the MVVM pattern) but I opened only a PR for the recycler view because, with all the current works on the database and the note (adding location/picture in the notes), I was not able to have a clean database to code the ViewModel. I implemented it on my own with some basic functionality so that next week I will just have to plug it with the right method's name.
I started my work for the internal storage by implementing a file system service to be able to read/write files in the internal storage of the app.
But I need to discuss with the team about async tasks and cached data.

This week wasn't the best one as we all have big pull requests and integrating all the stuff takes much more time than expected.
So I tried to review more PR than usual to help my teammates and move on with those big additions.

## Vincent

This week I managed to fix the tests for the note header image feature and I started to integrate that feature with the Firebase database. Unfortunately I didn’t have much time to review PRs.

I spent a lot of time reading docs because some details related to testing can be quite obscure. Some code samples linked by the official Android documentation contain calls to deprecated methods and I didn’t find a way around that. I hope that next week I will be able to get more work done.

## Sydney

This week I helped Leandro fix a spotless related issue on his machine. Spotless would tell that line endings were incorrect. On his machine, CRLF endings were expected but the whole project is formatted with LF endings.

This week was a nightmare. I was able to push forward the issue #151 on database persistency but it clearly took me a disproportionate time (multiple weeks and still trying to merge the related PR). Here are the reasons for the delay :
Technical debt : the database abstraction wasn’t designed from the start with asynchrony in mind. The database interface was constantly changing throughout the sprints. The android tests were not designed with the assumption that the database was an external dependency. Since we relied on a simulated “volatile” database in order to run tests locally until now, the tests had to be rewritten so that a mock database is injected when testing the UI. 
Lack of experience with Android and testing frameworks
Constantly having to merge conflicts

## Overall team

Overall this sprint was a good one, since we managed to fix an issue that happened on windows with gradle spotless and also we could add very cool features such as the GPS and the user parts. 
The sidebar and the draw activity are also ready because of conflicts so we couldn’t merge them now. We still have trouble as we assign us too much work so maybe in the next sprints we should aim for smaller tasks so that we don’t spend 12 hours on SDP and we would have more time to review all the PRs.
I think our team did pretty well and the next sprint will surely get some interesting new exciting features !


