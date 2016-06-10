
<nav class="header">
    <div class="logo-l"><img src="static/img/logo-l.png" width="430" height="326" alt="Призы всем!"/></div>
    <!-- end .logo-l-->
    <div class="logo-r"><img src="static/img/logo-r.png" width="494" height="302" alt="ХрусTeam"/></div>
    <!-- end .logo-r-->

    <div class="container">
      <nav class="menu">
        <a href="#">Об акции</a><a href="#">Призы</a><a href="#">Игра</a><a href="#">Победители</a> 
        <a href="#">Партнеры</a>

        
        <span ng-hide="user"><a href="/removeit-authorization-login">Вход{{user.email}}</a>/<span ng-hide="user"><a href="/removeit-authorization-reg">Регистрация{{user.email}}</a></span></span> 
        <span ng-show="user"><a href="#" data-sign-out-btn>Выход{{user.mail}}</a></span>
        

      </nav>
      <!-- end .menu--> 
    </div>
</nav>