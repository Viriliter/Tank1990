/*
 * Copyright (c) 2025.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tank1990.tile;

public enum TileConfiguration {
    BLOCK_CONF_FULL,
    BLOCK_CONF_1,
    BLOCK_CONF_2,
    BLOCK_CONF_3,
    BLOCK_CONF_4,
    BLOCK_CONF_5,
    BLOCK_CONF_6,
    BLOCK_CONF_7,
    BLOCK_CONF_8,
    BLOCK_CONF_9,
    BLOCK_CONF_10,
    BLOCK_CONF_11,
    BLOCK_CONF_12,
    BLOCK_CONF_13,
    BLOCK_CONF_14,
    BLOCK_CONF_15,
    BLOCK_CONF_16,
    BLOCK_CONF_17,
    BLOCK_CONF_18,
    BLOCK_CONF_19,
    BLOCK_CONF_20,
    BLOCK_CONF_21,
    BLOCK_CONF_22,
    BLOCK_CONF_23,
    BLOCK_CONF_24,
    BLOCK_CONF_25,
    BLOCK_CONF_26,
    BLOCK_CONF_27,
    BLOCK_CONF_28,
    BLOCK_CONF_29,
    BLOCK_CONF_30,
    BLOCK_CONF_31,
    BLOCK_CONF_32,
    BLOCK_CONF_33,
    BLOCK_CONF_34,
    BLOCK_CONF_35,
    BLOCK_CONF_36,
    BLOCK_CONF_37,
    BLOCK_CONF_38,
    BLOCK_CONF_39,
    BLOCK_CONF_40,
    BLOCK_CONF_41,
    BLOCK_CONF_42,
    BLOCK_CONF_43,
    BLOCK_CONF_44,
    BLOCK_CONF_45;

    public static TileConfiguration valueOf(int val) {
        return switch (val) {
            case (0) -> BLOCK_CONF_FULL;
            case (1) -> BLOCK_CONF_1;
            case (2) -> BLOCK_CONF_2;
            case (3) -> BLOCK_CONF_3;
            case (4) -> BLOCK_CONF_4;
            case (5) -> BLOCK_CONF_5;
            case (6) -> BLOCK_CONF_6;
            case (7) -> BLOCK_CONF_7;
            case (8) -> BLOCK_CONF_8;
            case (9) -> BLOCK_CONF_9;
            case (10) -> BLOCK_CONF_10;
            case (11) -> BLOCK_CONF_11;
            case (12) -> BLOCK_CONF_12;
            case (13) -> BLOCK_CONF_13;
            case (14) -> BLOCK_CONF_14;
            case (15) -> BLOCK_CONF_15;
            case (16) -> BLOCK_CONF_16;
            case (17) -> BLOCK_CONF_17;
            case (18) -> BLOCK_CONF_18;
            case (19) -> BLOCK_CONF_19;
            case (20) -> BLOCK_CONF_20;
            case (21) -> BLOCK_CONF_21;
            case (22) -> BLOCK_CONF_22;
            case (23) -> BLOCK_CONF_23;
            case (24) -> BLOCK_CONF_24;
            case (25) -> BLOCK_CONF_25;
            case (26) -> BLOCK_CONF_26;
            case (27) -> BLOCK_CONF_27;
            case (28) -> BLOCK_CONF_28;
            case (29) -> BLOCK_CONF_29;
            case (30) -> BLOCK_CONF_30;
            case (31) -> BLOCK_CONF_31;
            case (32) -> BLOCK_CONF_32;
            case (33) -> BLOCK_CONF_33;
            case (34) -> BLOCK_CONF_34;
            case (35) -> BLOCK_CONF_35;
            case (36) -> BLOCK_CONF_36;
            case (37) -> BLOCK_CONF_37;
            case (38) -> BLOCK_CONF_38;
            case (39) -> BLOCK_CONF_39;
            case (40) -> BLOCK_CONF_40;
            case (41) -> BLOCK_CONF_41;
            case (42) -> BLOCK_CONF_42;
            case (43) -> BLOCK_CONF_43;
            case (44) -> BLOCK_CONF_44;
            case (45) -> BLOCK_CONF_45;
            default -> throw new RuntimeException("Invalid Value for BlockConfiguration");
        };
    }
}
