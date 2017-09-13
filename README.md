# eclipselink-patched

This repository contains patches that improve the stability and performance of EclipseLink 2.6.5, at least for use cases at Acrolinx. The patches are applied by loading this library before EclipseLink on the classpath. 

## List of patches

* Improve query performance by making position of PRIOR keyword dependent on ReadAllQuery.Direction
* Order Acrolinx-specific tables in FROM clauses in a way that is performant on MSSQL
* MSSQL: Fix for issue when migrating from EclipseLink 2.5.x (https://bugs.eclipse.org/bugs/show_bug.cgi?id=499233)
* MSSQL: map Character[] and char[] to NTEXT instead of TEXT to create unicode-compatible schema
* MSSQL & Derby: increased max foreign key size to 36 to fix issue with duplicate constraint names due to EclipseLink naming strategy
* Derby: initializeConnectionData sets isSequenceSupported to avoid compatibility issues with older Derby DBs
* Oracle 10: batch writes are disabled in presence of optimistic locks (the default behavior of all other database platforms)
* Oracle 10: no locator is used for LOB-writing to fix issue when importing data twice

## Patch diff

https://github.com/acrolinx/eclipselink-patched/compare/original-classes...master?diff=unified&name=master&w=1