<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <a-space
          ><img src="../assets/logo.png" class="logo" />
          <div class="title">L OJ</div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="foonter">
        <a href="">版权信息</a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style>
#userLayout {
}

#userLayout .header {
  margin-bottom: 20px;
  text-align: center;
  background-color: rgb(30, 30, 26);
}

#userLayout .content {
  background: url(https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png)
    0% 0% / 100% 100%;
  margin-bottom: 20px;
  padding: 20px;
}

#userLayout .foonter {
  padding: 16px;
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}

#userLayout .logo {
  height: 48px;
  width: 48px;
}

#userLayout .title {
  padding-left: 10px;
  font-size: 25px;
  color: whitesmoke;
}
</style>
<script></script>
