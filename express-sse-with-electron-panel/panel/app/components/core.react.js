import React from 'react';
import Header from './header/header.react';
import Dashboard from './content/dashboard.react';

function Core() {
  return (
    <div className="core">
      <Header/>
      <Dashboard
        addSticky={() => {
          const xhr = new XMLHttpRequest();
          xhr.open('POST', 'http://localhost/add', true);
          xhr.send();
        }}
        updateSticky={() => {
          const xhr = new XMLHttpRequest();
          xhr.open('PUT', 'http://localhost/update', true);
          xhr.send();
        }}
        removeSticky={() => {
          const xhr = new XMLHttpRequest();
          xhr.open('DELETE', 'http://localhost/delete', true);
          xhr.send();
        }}
      />
    </div>
  );
}

export default Core;
