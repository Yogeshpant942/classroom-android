import {
    TableCell,
    TableRow,
    styled,
    tableCellClasses,
    Drawer as MuiDrawer,
    AppBar as MuiAppBar,
} from "@mui/material";

const drawerWidth = 240

export const StyledTableCell = styled(TableCell)(({ theme }) => ({
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: 'rgba(20, 20, 30, 0.7)',
        backdropFilter: 'blur(10px)',
        color: '#a0a0b0',
        textTransform: 'uppercase',
        letterSpacing: '1px',
        fontWeight: '700',
        fontSize: '0.75rem',
        borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
    },
    [`&.${tableCellClasses.body}`]: {
        fontSize: 14,
        color: '#e0e0e0',
        borderBottom: '1px solid rgba(255, 255, 255, 0.05)',
    },
}));

export const StyledTableRow = styled(TableRow)(({ theme }) => ({
    backgroundColor: 'rgba(255, 255, 255, 0.02)',
    transition: 'all 0.2s ease',
    '&:hover': {
        backgroundColor: 'rgba(255, 255, 255, 0.08) !important',
        transform: 'scale(1.002)',
        boxShadow: '0 4px 10px rgba(0,0,0,0.2)',
    },
    '&:last-child td, &:last-child th': {
        border: 0,
    },
}));

export const AppBar = styled(MuiAppBar, {
    shouldForwardProp: (prop) => prop !== 'open',
})(({ theme, open }) => ({
    zIndex: theme.zIndex.drawer + 1,
    backgroundColor: 'rgba(15, 15, 25, 0.85)',
    backdropFilter: 'blur(12px)',
    boxShadow: '0 4px 30px rgba(0, 0, 0, 0.5)',
    borderBottom: '1px solid rgba(255, 255, 255, 0.05)',
    transition: theme.transitions.create(['width', 'margin'], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    ...(open && {
        marginLeft: drawerWidth,
        width: `calc(100% - ${drawerWidth}px)`,
        transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    }),
}));

export const Drawer = styled(MuiDrawer, { shouldForwardProp: (prop) => prop !== 'open' })(
    ({ theme, open }) => ({
        '& .MuiDrawer-paper': {
            position: 'relative',
            whiteSpace: 'nowrap',
            width: drawerWidth,
            backgroundColor: 'rgba(10, 10, 15, 0.95)',
            borderRight: '1px solid rgba(255, 255, 255, 0.05)',
            boxShadow: '4px 0 30px rgba(0, 0, 0, 0.3)',
            transition: theme.transitions.create('width', {
                easing: theme.transitions.easing.sharp,
                duration: theme.transitions.duration.enteringScreen,
            }),
            boxSizing: 'border-box',
            ...(!open && {
                overflowX: 'hidden',
                transition: theme.transitions.create('width', {
                    easing: theme.transitions.easing.sharp,
                    duration: theme.transitions.duration.leavingScreen,
                }),
                width: theme.spacing(7),
                [theme.breakpoints.up('sm')]: {
                    width: theme.spacing(9),
                },
            }),
        },
    }),
);