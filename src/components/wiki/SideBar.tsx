import React from "react"
import {alpha, createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import InputBase from "@material-ui/core/InputBase"
import Toolbar from "@material-ui/core/Toolbar"
import {SideBarSize} from "../axios/Types"
import {Link, Redirect, useParams} from "react-router-dom"
import useSWR from "swr"

/**
 * Entry type for easy mapping the summary API call
 */
export type Entry =
    | { type: "HEADER", literal: string, }
    | { type: "ITEM", literal: string, destination: string }
    | { type: "LIST", children: Entry[] }

export const SideBar: React.FC<{ url: string }> = ({url}) => {
  const classes = useStyles()
  const {project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API call for the summary data
  const {data: summary, error} = useSWR<Entry[]>(`/summary/${project}`)

  // TODO right now this will redirect on any error, might wanna change to only 404 or something
  if (error) return <Redirect to="/404"/>

  // Function to check if the link is currently active
  const isActive = (destination: string) => destination === page

  // Could use some improvement make it more DRY
  const renderItem = (destination: string, literal: string) => {
    if (isActive(destination)) return <li><Link className={classes.active} to={destination}>{literal}</Link></li>
    return <li><Link to={destination}>{literal}</Link></li>
  }

  const renderList = (children: Entry[]) => {
    return (
        <ul>
          {
            children.map(entry => {
              if (entry.type === "ITEM") return renderItem(entry.destination, entry.literal)
              if (entry.type === "LIST") return renderList(entry.children)
              else return <></>
            })
          }
        </ul>
    )
  }

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
        <div className={classes.drawerContainer}>
          <div className={classes.sideList}>
            {
              summary?.map(entry => {
                if (entry.type === "ITEM") return renderItem(entry.destination, entry.literal)
                if (entry.type === "HEADER") return <h1>{entry.literal}</h1>
                if (entry.type === "LIST") return renderList(entry.children)
                return <></>
              })
            }
            <div className={classes.lastSpace}/>
          </div>
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
        overflow: "hidden",
        width: "100%",
        marginLeft: "auto",
        marginRight: "auto",
        position: "sticky",
        padding: "20px 0",
        maxHeight: "calc(100vh - 100px)",
        "&::after": {
          position: "absolute",
          zIndex: 10,
          left: 0,
          width: "100%",
          height: "10%",
          content: `""`,
          bottom: 0,
          background: `linear-gradient(180deg, ${theme.palette.background.default}00 0%, ${theme.palette.background.default}FF 50%)`,
        },
      },
      sideList: {
        overflowY: "auto",
        overflowX: "hidden",
        height: "100vh",
        padding: "0 15% 0 15%",
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
        "& h1": {
          display: "flex",
          border: 0,
          textDecoration: "none",
          fontWeight: "bold",
          fontSize: "1.3em",
        },
        "& ul": {
          padding: 0,
          margin: 0,
          marginBottom: "20%",
          listStyle: "none",
          display: "block",
        },
        "& li": {
          flexFlow: "row nowrap",
          padding: "6px 0",
          fontSize: "1.15em",
          lineHeight: "1.5",
          placeItems: "center",
          display: "list-item",
          cursor: "pointer",
        },
        "& a": {
          color: "#FFFFFFB3",
          "-webkit-transition": "opacity .2s,color .2s",
          transition: "opacity .2s,color .2s",
        },
        "& a:hover": {
          color: theme.palette.primary.main,
        },
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
        marginTop: "25px",
        marginBottom: "15px",
        width: "80%",
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
      active: {
        color: `${theme.palette.primary.main} !important`,
      },
      lastSpace: {
        height: "20%",
      }
    }),
)

export default SideBar
