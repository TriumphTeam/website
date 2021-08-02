import React from "react"
import {alpha, createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import List from "@material-ui/core/List"
import InputBase from "@material-ui/core/InputBase"
import Toolbar from "@material-ui/core/Toolbar"
import {BarText} from "./components/BarText"
import {Entry, SideBarSize} from "../axios/Types"
import BarLink from "./components/BarLink"
import {Redirect, useParams} from "react-router-dom"
import useSWR from "swr"

const drawerWidth = 300

export const SideBar: React.FC<{ url: string }> = ({url}) => {
  const classes = useStyles()
  const {project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API call for the summary data
  const {data: summary, error} = useSWR<{ entries: Entry[] }>(`/summary/${project}`)

  // TODO right now this will redirect on any error, might wanna change to only 404 or something
  if (error) return <Redirect to="/404"/>

  // Function to check if the link is currently active
  const isActive = (destination: string) => destination === page

  // TODO move search bar to its own component
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
                  input: classes.inputInput,
                  root: classes.inputRoot,
                }}
                inputProps={{"aria-label": "search"}}
            />
          </div>
          <List>
            {
              summary?.entries.map(entry => {
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
        width: SideBarSize,
        flexShrink: 0,
      },
      drawerPaper: {
        width: SideBarSize,
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
