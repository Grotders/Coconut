// check for saved 'darkMode' in localStorage
let darkMode = localStorage.getItem('darkMode'); 

const darkModeToggle = document.querySelector('#dark-mode-toggle');
const darkModeToggle2 = document.querySelector('#dark-button');

const enableDarkMode = () => {
  // 1. Add the class to the body
  darkModeToggle2.src="resources/images/karanlik.png"
  document.body.classList.add('darkmode');
  darkModeToggle.classList.replace('aydinlikmode','karanlikmode');
  // 2. Update darkMode in localStorage
  localStorage.setItem('darkMode', 'enabled');
  };

const disableDarkMode = () => {
  // 1. Remove the class from the body
  darkModeToggle2.src="resources/images/aydinlik.png"
  document.body.classList.remove('darkmode');
  darkModeToggle.classList.replace('karanlikmode','aydinlikmode');
  // 2. Update darkMode in localStorage 
  localStorage.setItem('darkMode', null);
};
 
// If the user already visited and enabled darkMode
// start things off with it on
if (darkMode === 'enabled') {
  enableDarkMode();
}

// When someone clicks the button
darkModeToggle.addEventListener('click', () => {
  // get their darkMode setting
  darkMode = localStorage.getItem('darkMode'); 
  
  // if it not current enabled, enable it
  if (darkMode !== 'enabled') {
    enableDarkMode();
  // if it has been enabled, turn it off  
  } else {  
    disableDarkMode(); 
  }
});




// document.getElementById('dark-mode').addEventListener('click', () => {
//     document.body.classList.toggle('dark');
//     localStorage.setItem('mode', document.body.classList);
// });
// if (localStorage.getItem('mode') != ''){
//     document.body.classList.add(localStorage.getItem('mode'));
// }