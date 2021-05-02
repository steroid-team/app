# Week 7

## Yago
This week the team altogether decided to give the utmost priority to merging [#151](https://github.com/steroid-team/app/issues/151) (data persistency via Firebase). Since we knew that pull request would have major conflicts with [#170](https://github.com/steroid-team/app/issues/170), I put the latter on hold until the former was merged.

In the meantime, I created a README file [(#185)](https://github.com/steroid-team/app/issues/185) and developed an editor for the notes’ content that allows giving rich format to the body (including images), see PR [(#191)](https://github.com/steroid-team/app/issues/191). I will complete the tests next week.

Once [#151](https://github.com/steroid-team/app/issues/151) was ready, I gave my second & third reviews and proceeded to solve the merge conflicts in [#170](https://github.com/steroid-team/app/issues/170). The amount of extra work has been simply crazy, but with a lot of effort it has finally been merged. Props to Théo and Sydney for helping me spot a concurrency issue along the process!

I also helped Leandro with the merge conflicts in [#174](https://github.com/steroid-team/app/issues/174), as well as Théo to debug some issues in [#119](https://github.com/steroid-team/app/issues/119).

Even though I feel like this especially intense sprint was necessary for the whole project, I am slightly concerned about keeping up with the current pace (i.e. interference with other subjects). Following Dorian’s advice, I will make some initial research before having my tasks assigned to make sure that the sprint tasks really take ~8h without leaving a backlog trail.

## Leandro (scrum master)
This week I finalized the tests for the drawing activity as well as the color picker until I could reach 100% coverage. I also started working on fitting a drawing into a note, but after talking with Yago, we found out it was better for me to work on that later, since he is doing practically the same thing with images. This week I worked less than eight hours, mainly because I found myself stuck with PRs #151 and #170. I waited for both of them to be merged before merging my branch to be sure not to add conflicts. Next week I will try to get more tasks done, and to focus more on reviewing PRs.

## Noah
This week I helped Sydney by taking one of his tasks (The audioRecorderActivity one). So I work on this new feature and first I begin with something really great in mind but that was way too difficult to do, but I only realized that later.. So I made only basics for this feature (record audio, play and stop the audio) and their tests. But since we wanted to merge first the PR from Sydney about Firebase and Yago’s one about the sidebar, I had to refactor my code to resolve the conflicts that it has created. Sadly, when I’m writing this I haven’t done a review because all the PR now are being refactored to resolve conflicts. Next week I’ll do better estimation and realistic thoughts about what I have to implement to waste less time working on something too difficult.

## Théo
This week I finalized my work on the local file system and the testing part. I needed to wait that some PRs were merged before I could finish the work on the viewmodel and design for the note fragment. So in the meantime, I decided to work on the drawing activity. Leandro agreed with me so I implemented little features and did some changes.
This week was fine for me, I managed to do what I wanted to do.
And we finally merged the two big PRs that refactor a lot of things. so now we can just add features without thinking of the architectural aspect of the app, that was for me the hardest one. Having a well structural application but not too complicated so we can add some features without the whole thing perfectly designed.
For the next week If the team agreed, I will try to add more features to the drawing activity because I like that we have this in our app and it will be our main sensor usage I think.

## Vincent
This week was in particular dedicated in merging 2 big PRs which refactored the whole app. It was important as if it was not done, new merge conflicts would constantly appear when new features are added. I helped a bit merging these, and I gave priority to address a few items from the code review. I preferred doing that over creating new features since it would be counterproductive to fix merge issues just after the big refactorings get in.

All in all I spent a bit less than 8 hours this week, as not that much help was needed to merge the big PRs. 

## Sydney
At last, issue 97 has been closed and its related PR #151 has finally been merged into the main branch. Now every action requiring data in the app is fetched and stored from and to a persistent database. This has been a hell of a task that required 4 weeks to finish completely and 40 hours worth of work just for that. So what do we learn from that …? 

First, we should define clear, complete and robust interfaces between modules in the code. That should be done well in advance and carefully thought. Having to change an interface can have, and that was the case here, a tremendous implication in the amount of work required for refactoring the code.

Second, we should have decided to split the work way earlier so that we could deliver some changes for each sprint anyway. However there was some misunderstanding on whether we had the possibility to shrink a task that in the end requires way more time than expected at the start of the sprints.

Finally, I think that fatally we can unfortunately not accurately estimate at 100% tasks when we are not experienced with the technology we are dealing with. Familiarizing with such a gigantic environment like Android cannot be done in a whole semester at 8 hours a week.

## Overall team
This week was very special, since we mostly dedicated to two big PRs and to pending reviews that needed some change. PRs #151 and #170 required a massive amount of work and a big part of it went into fixing merging issues, since both practically refactored the whole code. A consequence of this is that some other tasks got stuck because merging them would have added new issues. In the next weeks, we will try to divide the tasks better, so that no such great PR has to be made. The utilization of fragments made this huge change in our code inevitable, this is also due to our lack of experience in android development. But I think that this has been a positive week, and that it will help us organize our future work better.

