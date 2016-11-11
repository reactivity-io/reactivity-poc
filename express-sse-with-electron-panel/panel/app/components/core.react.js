import React from 'react';
import Header from './header/header.react';
import Dashboard from './content/dashboard.react';

function Core() {
  return (
    <div className="core">
      <Header/>
      <Dashboard
        addCard={() => {
          const xhr = new XMLHttpRequest();
          xhr.open('POST', 'http://localhost/addCard', true);
          xhr.send();
        }}
        updateCard={() => {
          const xhr = new XMLHttpRequest();
          xhr.open('PUT', 'http://localhost/updateCard', true);
          xhr.send();
        }}
        removeCard={() => {
          const xhr = new XMLHttpRequest();
          xhr.open('DELETE', 'http://localhost/deleteCard', true);
          xhr.send();
        }}
        addOrga={() => {
          const xhr = new XMLHttpRequest();
          xhr.open('POST', 'http://localhost/addOrga', true);
          xhr.send();
        }}
        removeOrga={() => {
          const xhr = new XMLHttpRequest();
          xhr.open('DELETE', 'http://localhost/deleteOrga', true);
          xhr.send();
        }}
      />
    </div>
  );
}

export default Core;
