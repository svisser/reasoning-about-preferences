grammar Prefs;

options {
language = Java;
}

tokens {
LIKE;
DISLIKE;
STRONG_LIKE;
STRONG_DISLIKE;
MINIMIZE;
BUDGET;
WEAK_BUDGET;
AND;
OR;
SUCCESS;
FAILURE;
CAUSED;
USED;
COMP_LT;
COMP_LTE;
COMP_E;
COMP_GTE;
COMP_GT;
APF_SEP;
}


@header {
package parser;

import bienvenu.*;
import bienvenu.BasicDesireFormula.BDFType;
import java.util.ArrayList;
}
@lexer::header {
package parser;

import bienvenu.*;
import bienvenu.BasicDesireFormula.BDFType;
import java.util.ArrayList;
}


AND	:	'and';
OR	:	'or';
APF_SEP	:	'>>';
BUDGET	:	'budget';
COLON	:	':';
COMMA	:	(SPACE* ',' SPACE*);
DISLIKE :	'dislike';
LIKE	:	'like';
MINIMIZE:	'minimize';
LPAREN	:	'(';
RPAREN	:	')';
SPACE	:	' ';
STRONG_DISLIKE
	:	'dislike+';
STRONG_LIKE
	:	'like+';
WEAK_BUDGET
	:	'budget-';
SUCCESS	:	'success';
FAILURE	:	'failure';
CAUSED	:	'caused';
USED	:	'used';
COMP_LT	:	'<';
COMP_LTE:	'<=';
COMP_E	:	'=';
COMP_GTE:	'>=';
COMP_GT	:	'>';
ID  :	('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*
    ;
INT :	'0'..'9'+
    ;

bdf returns [BasicDesireFormula result]
	: v0=bdf_like {$result = v0;}
	| v1=bdf_dislike {$result = v1;}
	| v4=bdf_and {$result = v4;}
	| v5=bdf_or {$result = v5;}
	| v6=bdf_strong_like {$result = v6;}
	| v7=bdf_strong_dislike {$result = v7;};

bdf_single returns [BasicDesireFormula result]
	: v2=bdf_minimize {$result = v2;}
	| v3=bdf_budget {$result = v3;}
	| v8=bdf_weak_budget {$result = v8;};
	
prog	:	gpf;

bdf_like returns [BasicDesireFormulaLike bdf_like]:
LIKE LPAREN ID RPAREN {bdf_like = new BasicDesireFormulaLike($ID.text);};

bdf_strong_like returns [BasicDesireFormulaLike bdf_strong_like]:
STRONG_LIKE LPAREN ID RPAREN {bdf_strong_like = new BasicDesireFormulaLike($ID.text, true);};
	
bdf_dislike returns [BasicDesireFormulaDislike bdf_dislike]:
DISLIKE LPAREN ID RPAREN {bdf_dislike = new BasicDesireFormulaDislike($ID.text);};

bdf_strong_dislike returns [BasicDesireFormulaDislike bdf_strong_dislike]:
STRONG_DISLIKE LPAREN ID RPAREN {bdf_strong_dislike = new BasicDesireFormulaDislike($ID.text, true);};

bdf_minimize returns [BasicDesireFormulaMinimize bdf_minimize]:
MINIMIZE LPAREN ID RPAREN {bdf_minimize = new BasicDesireFormulaMinimize($ID.text);};

bdf_budget returns [BasicDesireFormulaBudget bdf_budget]:
BUDGET LPAREN resource=ID COMMA b_min=INT COMMA b_max=INT RPAREN {bdf_budget = new BasicDesireFormulaBudget($resource.text, Integer.valueOf($b_min.text), Integer.valueOf($b_max.text));};

bdf_weak_budget returns [BasicDesireFormulaBudget bdf_budget_weak]:
WEAK_BUDGET LPAREN resource=ID COMMA b_min=INT COMMA b_max=INT RPAREN {bdf_budget_weak = new BasicDesireFormulaBudget($resource.text, Integer.valueOf($b_min.text), Integer.valueOf($b_max.text), true);};

bdf_and returns [BasicDesireFormulaDuo bdf_and]:
AND LPAREN left=bdf COMMA right=bdf RPAREN {bdf_and = new BasicDesireFormulaDuo(BDFType.AND, $left.result, $right.result);};

bdf_or returns [BasicDesireFormulaDuo bdf_or]:
OR LPAREN left=bdf COMMA right=bdf RPAREN {bdf_or = new BasicDesireFormulaDuo(BDFType.OR, $left.result, $right.result);};

bdf_cond returns [ConditionPreferenceFormula result]
	: v0=bdf_success {$result = v0;}
	| v1=bdf_failure {$result = v1;}
	| v2=bdf_caused {$result = v2;}
	| v4=bdf_comparison {$result = v4;};
//	| v3=bdf_used {$result = v3;};

bdf_success returns [ConditionPreferenceFormulaSuccess bdf_success]:
SUCCESS LPAREN ID RPAREN {bdf_success = new ConditionPreferenceFormulaSuccess($ID.text);};

bdf_failure returns [ConditionPreferenceFormulaFailure bdf_failure]:
FAILURE LPAREN ID RPAREN {bdf_failure = new ConditionPreferenceFormulaFailure($ID.text);};

bdf_caused returns [ConditionPreferenceFormulaCaused bdf_caused]:
CAUSED LPAREN goal=ID COMMA effect=ID RPAREN {bdf_caused = new ConditionPreferenceFormulaCaused($goal.text, $effect.text);};

bdf_used returns [ConditionPreferenceFormulaUsed bdf_used]:
USED LPAREN goal=ID COMMA resource=ID COMMA amount=INT RPAREN {bdf_used = new ConditionPreferenceFormulaUsed($goal.text, $resource.text, Integer.valueOf($amount.text));};

bdf_comparison returns [ConditionPreferenceFormulaUsed bdf_used]:
USED LPAREN goal=ID COMMA resource=ID COMMA amount=INT COMMA comparison=(COMP_LT|COMP_LTE|COMP_E|COMP_GTE|COMP_GT) RPAREN {bdf_used = new ConditionPreferenceFormulaUsed($goal.text, $resource.text, Integer.valueOf($amount.text), $comparison.text);};

apf returns [AtomicPreferenceFormula apf]
@init {
ArrayList<BasicDesireFormula> formulas = new ArrayList<BasicDesireFormula>();
ArrayList<Integer> values = new ArrayList<Integer>();
}:
f=bdf {formulas.add(f);} SPACE LPAREN v=INT {values.add(Integer.valueOf($v.text));} RPAREN (SPACE APF_SEP SPACE (g=bdf {formulas.add(g);} SPACE LPAREN w=INT {values.add(Integer.valueOf($w.text));} RPAREN))* {apf = new AtomicPreferenceFormula(formulas, values);}
	|	f=bdf_single {formulas.add(f);} SPACE LPAREN v=INT {values.add(Integer.valueOf($v.text));} RPAREN {apf = new AtomicPreferenceFormula(formulas, values);};

gpf_cond returns [GeneralPreferenceFormulaCondition gpf_cond]:
c=bdf_cond SPACE COLON SPACE f=gpf {gpf_cond = new GeneralPreferenceFormulaCondition($c.result, $f.result);};

gpf returns [GeneralPreferenceFormula result]:
(LPAREN g=goals RPAREN SPACE)?
	 (v0=apf {$result=v0; if ($g.result != null) { $result.addNodes($g.result); }}
	| v1=gpf_cond {$result=v1; if ($g.result != null ) { $result.addNodes($g.result);} });
	
goals returns [ArrayList<String> result]
@init {
result = new ArrayList<String>();
}:	n0=ID {result.add($n0.text);} (COMMA n1=ID {result.add($n1.text);})*;