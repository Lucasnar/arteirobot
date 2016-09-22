git add .
if [ -z "$1"]
then
    git commit -m "$1"
else
    git commit -m "Automagic commit."
fi
git push heroku master
