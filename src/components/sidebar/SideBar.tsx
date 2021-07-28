import React, {useEffect, useState} from "react"
import {alpha, createStyles, Theme, makeStyles, withStyles} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import List from "@material-ui/core/List"
import Divider from "@material-ui/core/Divider"
import InputBase from "@material-ui/core/InputBase"
import Toolbar from "@material-ui/core/Toolbar"
import {BarText} from "./components/BarText"
import api from "../axios/Api"
import {Entry} from "../axios/Types"
import BarLink from "./components/BarLink"
import ListItemText from "@material-ui/core/ListItemText"
import ListItem from "@material-ui/core/ListItem"
import {useParams} from "react-router-dom"

interface SideBarProp {
  entries: Entry[]
  url: string
}

const drawerWidth = 300

export const SideBar: React.FC<SideBarProp> = ({entries, url}) => {
  const classes = useStyles()

  const {path} = useParams<{ type?: string, name?: string, path?: string }>()

  const isActive = (destination: string) => destination === path

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
          <div className={classes.search}>
            <div className={classes.searchIcon}><i className="fas fa-search"/></div>
            <InputBase
                placeholder="Search…"
                classes={{
                  root: classes.inputRoot,
                  input: classes.inputInput,
                }}
                inputProps={{"aria-label": "search"}}
            />
          </div>
          <List>
            {
              entries.map(entry => {
                if (entry.type === "LINK") return <BarLink
                    text={entry.literal}
                    destination={`${url}/${entry.destination}`}
                    indent={entry.indent}
                    active={isActive(entry.destination)}
                />

                if (entry.type === "HEADER") return <BarText text={entry.literal}/>

                return <></>
              })
            }
          </List>
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
        border: "none",
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
        width: "100%",
        marginLeft: "auto",
        marginRight: "auto",
        padding: "20px 0",
      },
      content: {
        flexGrow: 1,
        padding: theme.spacing(3),
      },
      nested: {
        paddingLeft: theme.spacing(4),
      },
      search: {
        position: "relative",
        borderRadius: theme.shape.borderRadius,
        backgroundColor: alpha(theme.palette.common.white, 0.025),
        "&:hover": {
          backgroundColor: alpha(theme.palette.common.white, 0.05),
        },
        marginRight: "auto",
        marginLeft: "auto",
        marginTop: "10px",
        marginBottom: "0",
        width: "90%",
      },
      searchIcon: {
        padding: theme.spacing(0, 2),
        height: "100%",
        position: "absolute",
        pointerEvents: "none",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      },
      inputRoot: {
        color: "inherit",
      },
      inputInput: {
        padding: theme.spacing(1, 1, 1, 0),
        // vertical padding + font size from searchIcon
        paddingLeft: `calc(1em + ${theme.spacing(4)}px)`,
        transition: theme.transitions.create("width"),
        width: "100%",
        [theme.breakpoints.up("md")]: {
          width: "20ch",
        },
      },
    }),
)

export default SideBar