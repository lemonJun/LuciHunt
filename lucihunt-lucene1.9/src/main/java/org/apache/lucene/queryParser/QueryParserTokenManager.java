/* Generated By:JavaCC: Do not edit this line. QueryParserTokenManager.java */
package org.apache.lucene.queryParser;

import java.util.Vector;
import java.io.*;
import java.text.*;
import java.util.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.*;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Parameter;

public class QueryParserTokenManager implements QueryParserConstants {
    public java.io.PrintStream debugStream = System.out;

    public void setDebugStream(java.io.PrintStream ds) {
        debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_3(int pos, long active0) {
        switch (pos) {
            default:
                return -1;
        }
    }

    private final int jjStartNfa_3(int pos, long active0) {
        return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0), pos + 1);
    }

    private final int jjStopAtPos(int pos, int kind) {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        return pos + 1;
    }

    private final int jjStartNfaWithStates_3(int pos, int kind, int state) {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            return pos + 1;
        }
        return jjMoveNfa_3(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_3() {
        switch (curChar) {
            case 40:
                return jjStopAtPos(0, 12);
            case 41:
                return jjStopAtPos(0, 13);
            case 43:
                return jjStopAtPos(0, 10);
            case 45:
                return jjStopAtPos(0, 11);
            case 58:
                return jjStopAtPos(0, 14);
            case 91:
                return jjStopAtPos(0, 21);
            case 94:
                return jjStopAtPos(0, 15);
            case 123:
                return jjStopAtPos(0, 22);
            default:
                return jjMoveNfa_3(0, 0);
        }
    }

    private final void jjCheckNAdd(int state) {
        if (jjrounds[state] != jjround) {
            jjstateSet[jjnewStateCnt++] = state;
            jjrounds[state] = jjround;
        }
    }

    private final void jjAddStates(int start, int end) {
        do {
            jjstateSet[jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }

    private final void jjCheckNAddTwoStates(int state1, int state2) {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }

    private final void jjCheckNAddStates(int start, int end) {
        do {
            jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }

    private final void jjCheckNAddStates(int start) {
        jjCheckNAdd(jjnextStates[start]);
        jjCheckNAdd(jjnextStates[start + 1]);
    }

    static final long[] jjbitVec0 = { 0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL };
    static final long[] jjbitVec2 = { 0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL };

    private final int jjMoveNfa_3(int startState, int curPos) {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 33;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;) {
            if (++jjround == 0x7fffffff)
                ReInitRounds();
            if (curChar < 64) {
                long l = 1L << curChar;
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                            if ((0x7bffd0f8ffffd9ffL & l) != 0L) {
                                if (kind > 17)
                                    kind = 17;
                                jjCheckNAddStates(0, 6);
                            } else if ((0x100002600L & l) != 0L) {
                                if (kind > 6)
                                    kind = 6;
                            } else if (curChar == 34)
                                jjCheckNAdd(15);
                            else if (curChar == 33) {
                                if (kind > 9)
                                    kind = 9;
                            }
                            if (curChar == 38)
                                jjstateSet[jjnewStateCnt++] = 4;
                            break;
                        case 4:
                            if (curChar == 38 && kind > 7)
                                kind = 7;
                            break;
                        case 5:
                            if (curChar == 38)
                                jjstateSet[jjnewStateCnt++] = 4;
                            break;
                        case 13:
                            if (curChar == 33 && kind > 9)
                                kind = 9;
                            break;
                        case 14:
                            if (curChar == 34)
                                jjCheckNAdd(15);
                            break;
                        case 15:
                            if ((0xfffffffbffffffffL & l) != 0L)
                                jjCheckNAddTwoStates(15, 16);
                            break;
                        case 16:
                            if (curChar == 34 && kind > 16)
                                kind = 16;
                            break;
                        case 18:
                            if ((0x3ff000000000000L & l) == 0L)
                                break;
                            if (kind > 18)
                                kind = 18;
                            jjAddStates(7, 8);
                            break;
                        case 19:
                            if (curChar == 46)
                                jjCheckNAdd(20);
                            break;
                        case 20:
                            if ((0x3ff000000000000L & l) == 0L)
                                break;
                            if (kind > 18)
                                kind = 18;
                            jjCheckNAdd(20);
                            break;
                        case 21:
                            if ((0x7bffd0f8ffffd9ffL & l) == 0L)
                                break;
                            if (kind > 17)
                                kind = 17;
                            jjCheckNAddStates(0, 6);
                            break;
                        case 22:
                            if ((0x7bfff8f8ffffd9ffL & l) == 0L)
                                break;
                            if (kind > 17)
                                kind = 17;
                            jjCheckNAddTwoStates(22, 23);
                            break;
                        case 24:
                            if ((0x84002f0600000000L & l) == 0L)
                                break;
                            if (kind > 17)
                                kind = 17;
                            jjCheckNAddTwoStates(22, 23);
                            break;
                        case 25:
                            if ((0x7bfff8f8ffffd9ffL & l) != 0L)
                                jjCheckNAddStates(9, 11);
                            break;
                        case 26:
                            if (curChar == 42 && kind > 19)
                                kind = 19;
                            break;
                        case 28:
                            if ((0x84002f0600000000L & l) != 0L)
                                jjCheckNAddStates(9, 11);
                            break;
                        case 29:
                            if ((0xfbfffcf8ffffd9ffL & l) == 0L)
                                break;
                            if (kind > 20)
                                kind = 20;
                            jjCheckNAddTwoStates(29, 30);
                            break;
                        case 31:
                            if ((0x84002f0600000000L & l) == 0L)
                                break;
                            if (kind > 20)
                                kind = 20;
                            jjCheckNAddTwoStates(29, 30);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                            if ((0x97ffffff97ffffffL & l) != 0L) {
                                if (kind > 17)
                                    kind = 17;
                                jjCheckNAddStates(0, 6);
                            } else if (curChar == 126) {
                                if (kind > 18)
                                    kind = 18;
                                jjstateSet[jjnewStateCnt++] = 18;
                            }
                            if (curChar == 92)
                                jjCheckNAddStates(12, 14);
                            else if (curChar == 78)
                                jjstateSet[jjnewStateCnt++] = 11;
                            else if (curChar == 124)
                                jjstateSet[jjnewStateCnt++] = 8;
                            else if (curChar == 79)
                                jjstateSet[jjnewStateCnt++] = 6;
                            else if (curChar == 65)
                                jjstateSet[jjnewStateCnt++] = 2;
                            break;
                        case 1:
                            if (curChar == 68 && kind > 7)
                                kind = 7;
                            break;
                        case 2:
                            if (curChar == 78)
                                jjstateSet[jjnewStateCnt++] = 1;
                            break;
                        case 3:
                            if (curChar == 65)
                                jjstateSet[jjnewStateCnt++] = 2;
                            break;
                        case 6:
                            if (curChar == 82 && kind > 8)
                                kind = 8;
                            break;
                        case 7:
                            if (curChar == 79)
                                jjstateSet[jjnewStateCnt++] = 6;
                            break;
                        case 8:
                            if (curChar == 124 && kind > 8)
                                kind = 8;
                            break;
                        case 9:
                            if (curChar == 124)
                                jjstateSet[jjnewStateCnt++] = 8;
                            break;
                        case 10:
                            if (curChar == 84 && kind > 9)
                                kind = 9;
                            break;
                        case 11:
                            if (curChar == 79)
                                jjstateSet[jjnewStateCnt++] = 10;
                            break;
                        case 12:
                            if (curChar == 78)
                                jjstateSet[jjnewStateCnt++] = 11;
                            break;
                        case 15:
                            jjAddStates(15, 16);
                            break;
                        case 17:
                            if (curChar != 126)
                                break;
                            if (kind > 18)
                                kind = 18;
                            jjstateSet[jjnewStateCnt++] = 18;
                            break;
                        case 21:
                            if ((0x97ffffff97ffffffL & l) == 0L)
                                break;
                            if (kind > 17)
                                kind = 17;
                            jjCheckNAddStates(0, 6);
                            break;
                        case 22:
                            if ((0x97ffffff97ffffffL & l) == 0L)
                                break;
                            if (kind > 17)
                                kind = 17;
                            jjCheckNAddTwoStates(22, 23);
                            break;
                        case 23:
                            if (curChar == 92)
                                jjCheckNAddTwoStates(24, 24);
                            break;
                        case 24:
                            if ((0x6800000078000000L & l) == 0L)
                                break;
                            if (kind > 17)
                                kind = 17;
                            jjCheckNAddTwoStates(22, 23);
                            break;
                        case 25:
                            if ((0x97ffffff97ffffffL & l) != 0L)
                                jjCheckNAddStates(9, 11);
                            break;
                        case 27:
                            if (curChar == 92)
                                jjCheckNAddTwoStates(28, 28);
                            break;
                        case 28:
                            if ((0x6800000078000000L & l) != 0L)
                                jjCheckNAddStates(9, 11);
                            break;
                        case 29:
                            if ((0x97ffffff97ffffffL & l) == 0L)
                                break;
                            if (kind > 20)
                                kind = 20;
                            jjCheckNAddTwoStates(29, 30);
                            break;
                        case 30:
                            if (curChar == 92)
                                jjCheckNAddTwoStates(31, 31);
                            break;
                        case 31:
                            if ((0x6800000078000000L & l) == 0L)
                                break;
                            if (kind > 20)
                                kind = 20;
                            jjCheckNAddTwoStates(29, 30);
                            break;
                        case 32:
                            if (curChar == 92)
                                jjCheckNAddStates(12, 14);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = (int) (curChar >> 8);
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 077);
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                            if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                break;
                            if (kind > 17)
                                kind = 17;
                            jjCheckNAddStates(0, 6);
                            break;
                        case 15:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                                jjAddStates(15, 16);
                            break;
                        case 22:
                            if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                break;
                            if (kind > 17)
                                kind = 17;
                            jjCheckNAddTwoStates(22, 23);
                            break;
                        case 25:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                                jjCheckNAddStates(9, 11);
                            break;
                        case 29:
                            if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                break;
                            if (kind > 20)
                                kind = 20;
                            jjCheckNAddTwoStates(29, 30);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != 0x7fffffff) {
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }
            ++curPos;
            if ((i = jjnewStateCnt) == (startsAt = 33 - (jjnewStateCnt = startsAt)))
                return curPos;
            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_1(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 0x10000000L) != 0L) {
                    jjmatchedKind = 31;
                    return 4;
                }
                return -1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_1(int pos, long active0) {
        return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_1(int pos, int kind, int state) {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            return pos + 1;
        }
        return jjMoveNfa_1(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_1() {
        switch (curChar) {
            case 84:
                return jjMoveStringLiteralDfa1_1(0x10000000L);
            case 125:
                return jjStopAtPos(0, 29);
            default:
                return jjMoveNfa_1(0, 0);
        }
    }

    private final int jjMoveStringLiteralDfa1_1(long active0) {
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_1(0, active0);
            return 1;
        }
        switch (curChar) {
            case 79:
                if ((active0 & 0x10000000L) != 0L)
                    return jjStartNfaWithStates_1(1, 28, 4);
                break;
            default:
                break;
        }
        return jjStartNfa_1(0, active0);
    }

    private final int jjMoveNfa_1(int startState, int curPos) {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 5;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;) {
            if (++jjround == 0x7fffffff)
                ReInitRounds();
            if (curChar < 64) {
                long l = 1L << curChar;
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                            if ((0xfffffffeffffffffL & l) != 0L) {
                                if (kind > 31)
                                    kind = 31;
                                jjCheckNAdd(4);
                            }
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 6)
                                    kind = 6;
                            } else if (curChar == 34)
                                jjCheckNAdd(2);
                            break;
                        case 1:
                            if (curChar == 34)
                                jjCheckNAdd(2);
                            break;
                        case 2:
                            if ((0xfffffffbffffffffL & l) != 0L)
                                jjCheckNAddTwoStates(2, 3);
                            break;
                        case 3:
                            if (curChar == 34 && kind > 30)
                                kind = 30;
                            break;
                        case 4:
                            if ((0xfffffffeffffffffL & l) == 0L)
                                break;
                            if (kind > 31)
                                kind = 31;
                            jjCheckNAdd(4);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                        case 4:
                            if ((0xdfffffffffffffffL & l) == 0L)
                                break;
                            if (kind > 31)
                                kind = 31;
                            jjCheckNAdd(4);
                            break;
                        case 2:
                            jjAddStates(17, 18);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = (int) (curChar >> 8);
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 077);
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                        case 4:
                            if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                break;
                            if (kind > 31)
                                kind = 31;
                            jjCheckNAdd(4);
                            break;
                        case 2:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                                jjAddStates(17, 18);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != 0x7fffffff) {
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }
            ++curPos;
            if ((i = jjnewStateCnt) == (startsAt = 5 - (jjnewStateCnt = startsAt)))
                return curPos;
            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjMoveStringLiteralDfa0_0() {
        return jjMoveNfa_0(0, 0);
    }

    private final int jjMoveNfa_0(int startState, int curPos) {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 3;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;) {
            if (++jjround == 0x7fffffff)
                ReInitRounds();
            if (curChar < 64) {
                long l = 1L << curChar;
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                            if ((0x3ff000000000000L & l) == 0L)
                                break;
                            if (kind > 23)
                                kind = 23;
                            jjAddStates(19, 20);
                            break;
                        case 1:
                            if (curChar == 46)
                                jjCheckNAdd(2);
                            break;
                        case 2:
                            if ((0x3ff000000000000L & l) == 0L)
                                break;
                            if (kind > 23)
                                kind = 23;
                            jjCheckNAdd(2);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        default:
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = (int) (curChar >> 8);
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 077);
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        default:
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != 0x7fffffff) {
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }
            ++curPos;
            if ((i = jjnewStateCnt) == (startsAt = 3 - (jjnewStateCnt = startsAt)))
                return curPos;
            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_2(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 0x1000000L) != 0L) {
                    jjmatchedKind = 27;
                    return 4;
                }
                return -1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_2(int pos, long active0) {
        return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_2(int pos, int kind, int state) {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            return pos + 1;
        }
        return jjMoveNfa_2(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_2() {
        switch (curChar) {
            case 84:
                return jjMoveStringLiteralDfa1_2(0x1000000L);
            case 93:
                return jjStopAtPos(0, 25);
            default:
                return jjMoveNfa_2(0, 0);
        }
    }

    private final int jjMoveStringLiteralDfa1_2(long active0) {
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_2(0, active0);
            return 1;
        }
        switch (curChar) {
            case 79:
                if ((active0 & 0x1000000L) != 0L)
                    return jjStartNfaWithStates_2(1, 24, 4);
                break;
            default:
                break;
        }
        return jjStartNfa_2(0, active0);
    }

    private final int jjMoveNfa_2(int startState, int curPos) {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 5;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;) {
            if (++jjround == 0x7fffffff)
                ReInitRounds();
            if (curChar < 64) {
                long l = 1L << curChar;
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                            if ((0xfffffffeffffffffL & l) != 0L) {
                                if (kind > 27)
                                    kind = 27;
                                jjCheckNAdd(4);
                            }
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 6)
                                    kind = 6;
                            } else if (curChar == 34)
                                jjCheckNAdd(2);
                            break;
                        case 1:
                            if (curChar == 34)
                                jjCheckNAdd(2);
                            break;
                        case 2:
                            if ((0xfffffffbffffffffL & l) != 0L)
                                jjCheckNAddTwoStates(2, 3);
                            break;
                        case 3:
                            if (curChar == 34 && kind > 26)
                                kind = 26;
                            break;
                        case 4:
                            if ((0xfffffffeffffffffL & l) == 0L)
                                break;
                            if (kind > 27)
                                kind = 27;
                            jjCheckNAdd(4);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                        case 4:
                            if ((0xffffffffdfffffffL & l) == 0L)
                                break;
                            if (kind > 27)
                                kind = 27;
                            jjCheckNAdd(4);
                            break;
                        case 2:
                            jjAddStates(17, 18);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = (int) (curChar >> 8);
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 077);
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 0:
                        case 4:
                            if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                break;
                            if (kind > 27)
                                kind = 27;
                            jjCheckNAdd(4);
                            break;
                        case 2:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                                jjAddStates(17, 18);
                            break;
                        default:
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != 0x7fffffff) {
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }
            ++curPos;
            if ((i = jjnewStateCnt) == (startsAt = 5 - (jjnewStateCnt = startsAt)))
                return curPos;
            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    static final int[] jjnextStates = { 22, 25, 26, 29, 30, 27, 23, 18, 19, 25, 26, 27, 24, 28, 31, 15, 16, 2, 3, 0, 1, };

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                return ((jjbitVec2[i2] & l2) != 0L);
            default:
                if ((jjbitVec0[i1] & l1) != 0L)
                    return true;
                return false;
        }
    }

    public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, null, null, null, "\53", "\55", "\50", "\51", "\72", "\136", null, null, null, null, null, "\133", "\173", null, "\124\117", "\135", null, null, "\124\117", "\175", null, null, };
    public static final String[] lexStateNames = { "Boost", "RangeEx", "RangeIn", "DEFAULT", };
    public static final int[] jjnewLexState = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 2, 1, 3, -1, 3, -1, -1, -1, 3, -1, -1, };
    static final long[] jjtoToken = { 0xffffff81L, };
    static final long[] jjtoSkip = { 0x40L, };
    protected CharStream input_stream;
    private final int[] jjrounds = new int[33];
    private final int[] jjstateSet = new int[66];
    protected char curChar;

    public QueryParserTokenManager(CharStream stream) {
        input_stream = stream;
    }

    public QueryParserTokenManager(CharStream stream, int lexState) {
        this(stream);
        SwitchTo(lexState);
    }

    public void ReInit(CharStream stream) {
        jjmatchedPos = jjnewStateCnt = 0;
        curLexState = defaultLexState;
        input_stream = stream;
        ReInitRounds();
    }

    private final void ReInitRounds() {
        int i;
        jjround = 0x80000001;
        for (i = 33; i-- > 0;)
            jjrounds[i] = 0x80000000;
    }

    public void ReInit(CharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 4 || lexState < 0)
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
        else
            curLexState = lexState;
    }

    protected Token jjFillToken() {
        Token t = Token.newToken(jjmatchedKind);
        t.kind = jjmatchedKind;
        String im = jjstrLiteralImages[jjmatchedKind];
        t.image = (im == null) ? input_stream.GetImage() : im;
        t.beginLine = input_stream.getBeginLine();
        t.beginColumn = input_stream.getBeginColumn();
        t.endLine = input_stream.getEndLine();
        t.endColumn = input_stream.getEndColumn();
        return t;
    }

    int curLexState = 3;
    int defaultLexState = 3;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public Token getNextToken() {
        int kind;
        Token specialToken = null;
        Token matchedToken;
        int curPos = 0;

        EOFLoop: for (;;) {
            try {
                curChar = input_stream.BeginToken();
            } catch (java.io.IOException e) {
                jjmatchedKind = 0;
                matchedToken = jjFillToken();
                return matchedToken;
            }

            switch (curLexState) {
                case 0:
                    jjmatchedKind = 0x7fffffff;
                    jjmatchedPos = 0;
                    curPos = jjMoveStringLiteralDfa0_0();
                    break;
                case 1:
                    jjmatchedKind = 0x7fffffff;
                    jjmatchedPos = 0;
                    curPos = jjMoveStringLiteralDfa0_1();
                    break;
                case 2:
                    jjmatchedKind = 0x7fffffff;
                    jjmatchedPos = 0;
                    curPos = jjMoveStringLiteralDfa0_2();
                    break;
                case 3:
                    jjmatchedKind = 0x7fffffff;
                    jjmatchedPos = 0;
                    curPos = jjMoveStringLiteralDfa0_3();
                    break;
            }
            if (jjmatchedKind != 0x7fffffff) {
                if (jjmatchedPos + 1 < curPos)
                    input_stream.backup(curPos - jjmatchedPos - 1);
                if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
                    matchedToken = jjFillToken();
                    if (jjnewLexState[jjmatchedKind] != -1)
                        curLexState = jjnewLexState[jjmatchedKind];
                    return matchedToken;
                } else {
                    if (jjnewLexState[jjmatchedKind] != -1)
                        curLexState = jjnewLexState[jjmatchedKind];
                    continue EOFLoop;
                }
            }
            int error_line = input_stream.getEndLine();
            int error_column = input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;
            try {
                input_stream.readChar();
                input_stream.backup(1);
            } catch (java.io.IOException e1) {
                EOFSeen = true;
                error_after = curPos <= 1 ? "" : input_stream.GetImage();
                if (curChar == '\n' || curChar == '\r') {
                    error_line++;
                    error_column = 0;
                } else
                    error_column++;
            }
            if (!EOFSeen) {
                input_stream.backup(1);
                error_after = curPos <= 1 ? "" : input_stream.GetImage();
            }
            throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
        }
    }

}
