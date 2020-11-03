Clouds.jpg Photo by Konevi from Pexels---Free Stock image
https://www.pexels.com/photo/white-clouds-and-blue-sky-3789871/


I had a lot of trouble with this.  The one thing I could not get to work in time is the portfolio.
If you look at my code, I was trying to use a tab layout that would select between fragments,
but I had a very hard time getting my fragment to display correctly (Creating the fragment leads to
a null pointer exception when trying to call GetActivity() form within my fragment.  
I believe this is a threading problem).

What is working is my weather section, which makes use of a weather data api
to fetch the temperature and general weather description and displays it for 3
different locations. Additionally, as an extra component I added a hobbies section
where I was able to embed a SoundCloud music player using a web View.
