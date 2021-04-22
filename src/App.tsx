import React from 'react';
import {makeStyles} from "@material-ui/core";

const baseStyle = makeStyles(theme => ({
  body: {
    background: theme.palette.background.default,
  },
}))

function App() {
  return (
      <div className="App">
        <h1>Hello</h1>
      </div>
  );
}

export default App;
