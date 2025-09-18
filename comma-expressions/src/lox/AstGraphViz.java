package lox;

import java.io.PrintWriter;

import lox.Expr.Binary;
import lox.Expr.Grouping;
import lox.Expr.Literal;
import lox.Expr.Unary;

class AstGraphViz implements Expr.Visitor<String>{
    StringBuilder builder;
    AstGraphViz() {
        builder = new StringBuilder();
    }
    void outputDotFile(Expr expr, String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename);
            writer.write(print(expr));
            writer.close();
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return;
        }
    }
    String print(Expr expr) {
        builder.append("digraph G {\n");
        builder.append("node [shape=box];\n");
        expr.accept(this);
        builder.append("}\n");
        return builder.toString();
    }

    public String visitBinaryExpr(Binary expr) {
        String nodeId = "node" + System.identityHashCode(expr);
        builder.append(String.format("%s [label=\"%s\"];\n", nodeId, expr.operator.lexeme));
        String leftId = expr.left.accept(this);
        String rightId = expr.right.accept(this);
        builder.append(String.format("%s -> %s;\n", nodeId, leftId));
        builder.append(String.format("%s -> %s;\n", nodeId, rightId));
        return nodeId;
    }

    public String visitGroupingExpr(Grouping expr) {
        String nodeId = "node" + System.identityHashCode(expr);
        builder.append(String.format("%s [label=\"group\"];\n", nodeId));
        String exprId = expr.expression.accept(this);
        builder.append(String.format("%s -> %s;\n", nodeId, exprId));
        return nodeId;
    }

    public String visitLiteralExpr(Literal expr) {
        String nodeId = "node" + System.identityHashCode(expr);
        String label = (expr.value == null) ? "nil" : expr.value.toString();
        builder.append(String.format("%s [label=\"%s\"];\n", nodeId, label));
        return nodeId;
    }

    public String visitUnaryExpr(Unary expr) {
        String nodeId = "node" + System.identityHashCode(expr);
        builder.append(String.format("%s [label=\"%s\"];\n", nodeId, expr.operator.lexeme));
        String rightId = expr.right.accept(this);
        builder.append(String.format("%s -> %s;\n", nodeId, rightId));
        return nodeId;
    }
}