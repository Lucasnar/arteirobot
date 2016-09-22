COMMIT_MESSAGE="$1"
git add .
if [ $COMMIT_MESSAGE != "" ]
then
    git commit -m $COMMIT_MESSAGE
else
    git commit -m "Automagic commit."
fi
git push heroku master
