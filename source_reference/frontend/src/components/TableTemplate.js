import React, { useState } from 'react'
import { StyledTableCell, StyledTableRow } from './styles';
import { Table, TableBody, TableContainer, TableHead, TablePagination, Box } from '@mui/material';

const TableTemplate = ({ buttonHaver: ButtonHaver, columns, rows }) => {
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    return (
        <Box sx={{ 
            backgroundColor: 'rgba(255, 255, 255, 0.03)', 
            backdropFilter: 'blur(10px)', 
            borderRadius: '16px', 
            border: '1px solid rgba(255, 255, 255, 0.05)',
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
            overflow: 'hidden'
        }}>
            <TableContainer>
                <Table stickyHeader aria-label="sticky table" sx={{ borderCollapse: 'separate', borderSpacing: '0 8px', padding: '0 16px' }}>
                    <TableHead>
                        <StyledTableRow>
                            {columns.map((column) => (
                                <StyledTableCell
                                    key={column.id}
                                    align={column.align}
                                    style={{ minWidth: column.minWidth }}
                                >
                                    {column.label}
                                </StyledTableCell>
                            ))}
                            <StyledTableCell align="center">
                                Actions
                            </StyledTableCell>
                        </StyledTableRow>
                    </TableHead>
                    <TableBody>
                        {rows
                            .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                            .map((row) => {
                                return (
                                    <StyledTableRow hover role="checkbox" tabIndex={-1} key={row.id}>
                                        {columns.map((column) => {
                                            const value = row[column.id];
                                            return (
                                                <StyledTableCell key={column.id} align={column.align}>
                                                    {
                                                        column.format && typeof value === 'number'
                                                            ? column.format(value)
                                                            : value
                                                    }
                                                </StyledTableCell>
                                            );
                                        })}
                                        <StyledTableCell align="center">
                                            <ButtonHaver row={row} />
                                        </StyledTableCell>
                                    </StyledTableRow>
                                );
                            })}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                rowsPerPageOptions={[5, 10, 25, 100]}
                component="div"
                count={rows.length}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={(event, newPage) => setPage(newPage)}
                onRowsPerPageChange={(event) => {
                    setRowsPerPage(parseInt(event.target.value, 5));
                    setPage(0);
                }}
                sx={{ 
                    color: '#e0e0e0', 
                    borderTop: '1px solid rgba(255, 255, 255, 0.1)'
                }}
            />
        </Box>
    )
}

export default TableTemplate