grammar Calc;

@parser::header { package com.example.parser; }
@lexer::header { package com.example.parser; }

expr:   left=expr op=('*'|'/') right=expr   # MulDiv
    |   left=expr op=('+'|'-') right=expr   # AddSub
    |   INT                                 # Int
    |   '(' expr ')'                        # Parens
    ;

INT: [0-9]+;
WS: [ \t\r\n]+ -> skip;