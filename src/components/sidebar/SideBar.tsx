import React, {useEffect, useState} from "react"
import {createStyles, Theme, makeStyles, withStyles} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import List from "@material-ui/core/List"
import Divider from "@material-ui/core/Divider"
import Collapse from "@material-ui/core/Collapse"
import ListItem from "@material-ui/core/ListItem"
import ListItemText from "@material-ui/core/ListItemText"
import Toolbar from "@material-ui/core/Toolbar"
import {BarText} from "./components/BarText"
import api from "../axios/Api"
import {BarDropdown} from "./components/BarDropdown"

const drawerWidth = 350

export default function SideBar() {
  const classes = useStyles()
  const [summary, setSummary] = useState([])

  useEffect(() => {
    api.get("/summary/triumph-gui")
        .then(res => setSummary(res.data.entries))
        .catch(err => {
          console.log(err)
        })
  }, [])

  return (
      <Drawer
          className={classes.drawer}
          variant="permanent"
          classes={{
            paper: classes.drawerPaper,
          }}
      >
        <Toolbar className={classes.toolBar}/>
        <div className={classes.drawerContainer}>
          <List>
            {summary.map(entry => {
                const type = entry["type"]
                if (type === "MENU") {
                  const main = entry["main"]
                  const children = entry["children"]

                  return (<BarDropdown text={main["literal"]} defaultOpen={false}/>)
                }

                if (type === "LINK") {
                  return (
                      <ListItem button>
                        <ListItemText primary={entry["literal"]}/>
                      </ListItem>
                  )
                }

                return (<BarText text={entry["literal"]}/>)
                /*
                <>
                <BarDropdown text={"Test"} defaultOpen={false}/>
                <ListItem button key={text}>
                  <ListItemText primary={text}/>
                </ListItem>
              </>
                * */
            })}
          </List>
          <Divider/>
        </div>
      </Drawer>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      root: {
        display: "flex",
      },
      toolBar: {
        marginBottom: "10px",
      },
      drawer: {
        width: drawerWidth,
        flexShrink: 0,
      },
      drawerPaper: {
        width: drawerWidth,
      },
      drawerContainer: {
        overflow: "auto",
        "&::-webkit-scrollbar": {
          width: 7,
        },
        "&::-webkit-scrollbar-track": {
          boxShadow: `inset 0 0 6px rgba(0, 0, 0, 0.3)`,
        },
        "&::-webkit-scrollbar-thumb": {
          backgroundColor: "#3C3E41",
          borderRadius: "25px",
        },
      },
      content: {
        flexGrow: 1,
        padding: theme.spacing(3),
      },
      nested: {
        paddingLeft: theme.spacing(4),
      },
    }),
)
