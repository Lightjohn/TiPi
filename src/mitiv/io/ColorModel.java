/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014 the MiTiV project, http://mitiv.univ-lyon1.fr/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package mitiv.io;

import java.awt.image.BufferedImage;

import mitiv.array.Array2D;
import mitiv.array.Array3D;
import mitiv.array.Byte2D;
import mitiv.array.Byte3D;
import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.DoubleArray;
import mitiv.array.Float2D;
import mitiv.array.Float3D;
import mitiv.array.FloatArray;
import mitiv.array.Int2D;
import mitiv.array.Int3D;
import mitiv.array.Long2D;
import mitiv.array.Long3D;
import mitiv.array.ShapedArray;
import mitiv.array.Short2D;
import mitiv.array.Short3D;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.base.indexing.Range;
import mitiv.exception.IllegalTypeException;


/**
 * Different color models used when interpreting shaped arrays as images or
 * when selecting different color channels or applying color conversion.
 *
 * <h2>Conventions</h2>
 * <p>
 * A simple heuristic is applied to determine the color model of a shaped
 * array when it is interpreted as an image.  The rules are:
 * <ol>
 * <li>a 2D shaped array of any type is a gray-scale image with color
 *     model {@link #GRAY};</li>
 * <li>a 3D shaped array of any type with first dimension equals to 3 is
 *     a RGB image with color model {@link #RGB};</li>
 * <li>a 3D shaped array of type {@code byte} and with first dimension
 *     equals to 4 and is a RGBA image with color model {@link #RGBA};</li>
 * <li>anything else corresponds to the the color model {@link #NONE}.</li>
 * </ol>
 * </p><p>
 * These conventions are assumed by the various conversion routines, <i>e.g.</i>
 * {@link DataFormat#makeBufferedImage} to convert a buffered image to a
 * shaped array or {@link DataFormat#imageToShapedArray} to perform the
 * opposite conversion.
 * </p><p>
 * When reading data as a shaped array, the method {@link #guessColorModel}
 * can be used to determine the color model following the aforementioned
 * rules.
 * </p><p>
 * The methods {@link #filterImageAsFloat} and {@link #filterImageAsDouble}
 * may be used to filter or convert an image represented by a shaped array
 * into a specific color model with a floating-point representaion.  These
 * methods can also be used to extract a specific color channel or the alpha
 * channel.
 * </p>
 *
 * <h2>Gray-scale images</h2>
 * <p>
 * A gray-scale image is stored into a shaped array as a 2D array with the
 * first dimension equals to the width of the image and the second dimension
 * equals to the height of the image.  The pixels of a gray-scale image have a
 * single value: their intensity or their gray level.  The gray level of a
 * shaped array interpreted as a gray-scale image can be accessed as follows:
 * <pre>
 *      gray = img.get(x,y);
 * </pre>
 * where <b>img</b> is an {@link Array2D} object and (<b>x</b>,<b>y</b>)
 * are the pixel coordinates.
 * </p>
 *
 * <h2>RGB images</h2>
 * <p>
 * A RGB image is stored into a shaped array as a 3D array with the first
 * dimension equals to 3, the second dimension equals to the width of the
 * image and the third dimension equals to the height of the image.  The
 * pixels of a RGB image have three colors: red (R), blue (B) and green (G).
 * The colors of a shaped array interpreted as a RGB image can be accessed as
 * follows:
 * <pre>
 *      red   = img.get(0,x,y);
 *      green = img.get(1,x,y);
 *      blue  = img.get(2,x,y);
 * </pre>
 * where <b>img</b> is an {@link Array3D} object and (<b>x</b>,<b>y</b>)
 * are the pixel coordinates.
 * </p>
 *
 * <h2>RGBA images</h2>
 * <p>
 * A RGBA image is stored into a shaped array as a 3D array with the first
 * dimension equals to 4, the second dimension equals to the width of the
 * image and the third dimension equals to the height of the image.  The
 * pixels of a RGBA image have four values: red (R), blue (B), green (G) and
 * alpha (A).  The values of a shaped array interpreted as a RGBA image can be
 * accessed as follows:
 * <pre>
 *      red   = img.get(0,x,y);
 *      green = img.get(1,x,y);
 *      blue  = img.get(2,x,y);
 *      alpha = img.get(3,x,y);
 * </pre>
 * where <b>img</b> is an {@link Array3D} object and (<b>x</b>,<b>y</b>)
 * are the pixel coordinates.
 * </p><p>

 * Currently, to avoid ambiguities about the interpretation of the alpha
 * channel, we only support RGBA images with {@code byte} values.  These
 * values are understood as being unsigned.  A value of zero for alpha
 * corresponds to a transparent pixel, the highest possible value of alpha,
 * <i>i.e.</i> {@code 0xFF}, corresponds to an opaque pixel.  It is assumed by
 * the various conversion routines, that the color levels are not
 * pre-multiplied by the alpha value.  When converting a buffered image of
 * type {@link BufferedImage#TYPE_INT_ARGB_PRE} or
 * {@link BufferedImage#TYPE_4BYTE_ABGR}, the pre-multiplication is
 * reversed to follow this convention.
 * </p>
 *
 * @author Éric Thiébaut.
 *
 */
public enum ColorModel {
    /** The red channel of an RGB or RGBA image. */
    RED(1, "red channel"),

    /** The green channel of an RGB or RGBA image. */
    GREEN(1, "green channel"),

    /** The blue channel of an RGB or RGBA image. */
    BLUE(1, "blue channel"),

    /** The alpha channel of an RGBA image. */
    ALPHA(1, "alpha channel"),

    /** A gray-scale image. */
    GRAY(1, "intensity or gray-scale"),

    /**
     * A RGB image.
     * @see {@link guessColorModel} for a discussion about the conventions.
     */
    RGB(3, "red-green-blue"),

    /**
     * A RGBA image.
     * @see {@link guessColorModel} for a discussion about the conventions.
     */
    RGBA(4, "red-green-blue-alpha"),

    /** Any non-image array. */
    NONE(-1, "non-image array");

    private final int bands;
    private final String description;
    private ColorModel(int bands, String descr) {
        this.bands = bands;
        this.description = descr;
    }

    /** Get the number of bands of the color model. */
    public int bands() {
        return bands;
    }

    /** Get the description of the color model. */
    public String description() {
        return description;
    }

    /** Get a string representation of the color model. */
    @Override
    public String toString() {
        return description;
    }

    /**
     * Guess the color model of a shaped array.
     * <p>
     * This method applies a simple heuristic (see {@link ColorModel}) to
     * determine the color model of a shaped array when it is interpreted
     * as an image.
     * </p>
     * @param arr - The shaped array.
     *
     * @return One of the following color models: {@link #GRAY},
     *          {@link #RGB}, {@link #RGBA} or {@link #NONE}.
     *
     * @see {@link ColorModel}, {@link #GRAY}, {@link #RGB}, {@link #RGBA} or {@link #NONE}.
     */
    static public ColorModel guessColorModel(ShapedArray arr) {
        int rank = arr.getRank();
        if (rank == 2) {
            return GRAY;
        }
        if (rank == 3) {
            int bands = arr.getDimension(0);
            if (bands == 3) {
                return RGB;
            }
            if (bands == 4 && arr.getType() == Traits.BYTE) {
                return RGBA;
            }
        }
        return NONE;
    }


    /*=======================================================================*/
    /* COLORS */

    /**
     * Convert red, green and blue levels in a gray level.
     * @param red   - The red level.
     * @param green - The green level.
     * @param blue  - The blue level.
     * @return The gray level.
     */
    public static double colorToGrey(double red, double green, double blue) {
        return 0.2126*red + 0.7152*green + 0.0722*blue;
    }

    /**
     * Convert red, green and blue levels in a gray level.
     * @param red   - The red level.
     * @param green - The green level.
     * @param blue  - The blue level.
     * @return The gray level.
     */
    public static float colorToGrey(float red, float green, float blue) {
        return 0.2126F*red + 0.7152F*green + 0.0722F*blue;
    }

    /**
     * Convert red, green and blue levels in a gray level.
     * @param red   - The red level.
     * @param green - The green level.
     * @param blue  - The blue level.
     * @return The gray level.
     */
    public static int colorToGrey(int red, int green, int blue) {
        return Math.round(0.2126F*red + 0.7152F*green + 0.0722F*blue);
    }

    /* COLOR MODELS
     * Below is how to decode an ARGB pixel value:
     * <pre>
     *   int argb = image.getRGB(i,j);
     *   int red   = (argb)&0xFF;
     *   int green = (argb>>8)&0xFF;
     *   int blue  = (argb>>16)&0xFF;
     *   int alpha = (argb>>24)&0xFF;
     * </pre>
     */

    //private static int[] grayTable = makeGrayTable();
    //private static int[] makeGrayTable() {
    //    int[] color = new int[256];
    //    int OPAQUE = 255;
    //    for (int gray = 0; gray < 256; ++gray) {
    //        color[gray] = (OPAQUE<<24)|(gray<<16)|(gray<<8)|gray;
    //    }
    //    return color;
    //}


    /**
     * Filter the channels of an image stored as a shaped array to produce
     * a floating point image suitable for inverse problem data processing.
     * <p>
     * This method must be called after converting a buffered image with
     * {@link #imageToShapedArray}.  The method is lazy: the input array is
     * returned if possible.
     * </p>
     * @param image      - The array to filter.
     * @param colorModel - The color model for the result.
     * @return A FloatArray object with shape {width,height} for a
     * grayscale image, with shape {depth,width,height} for a RGB or RGBA
     * image (depth = 3 or 4 respectively).
     */
    public static FloatArray filterImageAsFloat(ShapedArray arr, ColorModel colorModel) {
        int type = arr.getType();
        Shape shape = arr.getShape();
        int rank = shape.rank();
        int depth = -1;
        boolean colored = false;
        if (rank == 2) {
            depth = 1;
        } else if (rank == 3) {
            depth = shape.dimension(0);
            if (depth == 3) {
                colored = true;
            } else if (depth == 4 && type == Traits.BYTE) {
                colored = true;
            } else {
                depth = -1;
            }
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Images must be WIDTH x HEIGHT arrays, 3 x WIDTH x HEIGHT arrays or 4 x WIDTH x HEIGHT byte arrays.");

        }
        final int width = shape.dimension(rank - 2);
        final int height = shape.dimension(rank - 1);

        /* Perhaps simply getting a slice is sufficient. */
        ShapedArray view = null;

        if (colorModel == ColorModel.GRAY) {
            if (colored) {
                Float2D dst = Float2D.create(width, height);
                switch (type) {
                case Traits.BYTE: {
                    Byte3D src = (Byte3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)((int)src.get(0,x,y) & 0xFF);
                            float green = (float)((int)src.get(1,x,y) & 0xFF);
                            float blue  = (float)((int)src.get(2,x,y) & 0xFF);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.SHORT: {
                    Short3D src = (Short3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)src.get(0,x,y);
                            float green = (float)src.get(1,x,y);
                            float blue  = (float)src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.INT: {
                    Int3D src = (Int3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)src.get(0,x,y);
                            float green = (float)src.get(1,x,y);
                            float blue  = (float)src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.LONG: {
                    Long3D src = (Long3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)src.get(0,x,y);
                            float green = (float)src.get(1,x,y);
                            float blue  = (float)src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.FLOAT: {
                    Float3D src = (Float3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = src.get(0,x,y);
                            float green = src.get(1,x,y);
                            float blue  = src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.DOUBLE: {
                    Double3D src = (Double3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)src.get(0,x,y);
                            float green = (float)src.get(1,x,y);
                            float blue  = (float)src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                default:
                    throw new IllegalTypeException();
                }
                return dst;
            } else {
                view = arr;
            }

       } else if (colorModel == ColorModel.RGB) {

            if (depth == 3) {
                view = arr;
            } else if (depth == 4) {
                view = ((Array3D)arr).view(new Range(0,2), null, null);
            } else {
                /* Make an RGB image from a gray image.  (FIXME: Things could
                 * be much simpler if only we can insert a dimension.) */
                Float3D dst = Float3D.create(3, width, height);
                switch (type) {
                case Traits.BYTE: {
                    Byte2D src = (Byte2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)((int)src.get(x,y) & 0xFF);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.SHORT: {
                    Short2D src = (Short2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.INT: {
                    Int2D src = (Int2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.LONG: {
                    Long2D src = (Long2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.FLOAT: {
                    Float2D src = (Float2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.DOUBLE: {
                    Double2D src = (Double2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                default:
                    throw new IllegalTypeException();
                }
                return dst;
            }

        } else if (colorModel == ColorModel.RGBA) {

            /* Create new floating point RGBA image. */
            Float3D dst = Float3D.create(4, width, height);

            if (depth == 1) {

                /* Make an floating point RGBA image from a gray image. */
                final float opaque = 1.0F;
                switch (type) {
                case Traits.BYTE: {
                    Byte2D src = (Byte2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)((int)src.get(x,y) & 0xFF);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.SHORT: {
                    Short2D src = (Short2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.INT: {
                    Int2D src = (Int2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.LONG: {
                    Long2D src = (Long2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.FLOAT: {
                    Float2D src = (Float2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.DOUBLE: {
                    Double2D src = (Double2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float value = (float)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                default:
                    throw new IllegalTypeException();
                }

            } else if (depth == 3) {

                /* Make an floating point RGBA image from a RGB image. */
                final float opaque = 1;
                switch (type) {
                case Traits.BYTE: {
                    Byte3D src = (Byte3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)((int)src.get(0,x,y) & 0xFF);
                            float green = (float)((int)src.get(1,x,y) & 0xFF);
                            float blue  = (float)((int)src.get(2,x,y) & 0xFF);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.SHORT: {
                    Short3D src = (Short3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)src.get(0,x,y);
                            float green = (float)src.get(1,x,y);
                            float blue  = (float)src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.INT: {
                    Int3D src = (Int3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)src.get(0,x,y);
                            float green = (float)src.get(1,x,y);
                            float blue  = (float)src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.LONG: {
                    Long3D src = (Long3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)src.get(0,x,y);
                            float green = (float)src.get(1,x,y);
                            float blue  = (float)src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.FLOAT: {
                    Float3D src = (Float3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = src.get(0,x,y);
                            float green = src.get(1,x,y);
                            float blue  = src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.DOUBLE: {
                    Double3D src = (Double3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            float red   = (float)src.get(0,x,y);
                            float green = (float)src.get(1,x,y);
                            float blue  = (float)src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                default:
                    throw new IllegalTypeException();
                }

            } else {

                /* Make an floating point RGBA image from a RGBA byte image. */
                Byte3D src = (Byte3D)arr;
                final float scale = 1.0F/255.0F;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        float red   = (float)((int)src.get(0,x,y) & 0xFF);
                        float green = (float)((int)src.get(1,x,y) & 0xFF);
                        float blue  = (float)((int)src.get(2,x,y) & 0xFF);
                        float alpha = (float)((int)src.get(3,x,y) & 0xFF)*scale;
                        dst.set(0,x,y, red);
                        dst.set(1,x,y, green);
                        dst.set(2,x,y, blue);
                        dst.set(3,x,y, alpha);
                    }
                }
            }

            return dst;

        } else if (colorModel == ColorModel.RED) {

            view = (depth == 1 ? arr : ((Array3D)arr).slice(0, 0));

        } else if (colorModel == ColorModel.GREEN) {

            view = (depth == 1 ? arr : ((Array3D)arr).slice(1, 0));

        } else if (colorModel == ColorModel.BLUE) {

            view = (depth == 1 ? arr : ((Array3D)arr).slice(2, 0));

        } else if (colorModel == ColorModel.ALPHA) {

            Float2D dst = Float2D.create(width, height);
            if (depth == 4) {
                /* Extract the alpha channel of an RGBA byte image. */
                Byte3D src = (Byte3D)arr;
                final float scale = 1.0F/255.0F;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        float alpha = (float)((int)src.get(3,x,y) & 0xFF)*scale;
                        dst.set(x,y, alpha);
                    }
                }
            } else {
                dst.fill(1.0F);
            }
            return dst;

        } else  {

            throw new IllegalArgumentException("Unkwnon color filter");
        }

        /* If arrive here, we just have to convert the view.   At most what is needed
           is pixel unpacking and data conversion. */
        if (view == arr && type == Traits.FLOAT) {
            /* Never forget to be lazy. */
            return (FloatArray)arr;
        }
        shape = view.getShape();
        rank = view.getRank();
        if (rank == 2) {
            depth = 1;
        } else {
            depth = shape.dimension(0);
        }
        switch (type) {
        case Traits.BYTE:
             if (depth == 1) {
                 Float2D dst = Float2D.create(width, height);
                 Byte2D src = (Byte2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float value = (float)((int)src.get(x,y) & 0xFF);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Float3D dst = Float3D.create(depth, width, height);
                 Byte3D src = (Byte3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float red   = (float)((int)src.get(0,x,y) & 0xFF);
                         float green = (float)((int)src.get(1,x,y) & 0xFF);
                         float blue  = (float)((int)src.get(2,x,y) & 0xFF);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.SHORT:
             if (depth == 1) {
                 Float2D dst = Float2D.create(width, height);
                 Short2D src = (Short2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float value = (float)src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Float3D dst = Float3D.create(depth, width, height);
                 Short3D src = (Short3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float red   = (float)src.get(0,x,y);
                         float green = (float)src.get(1,x,y);
                         float blue  = (float)src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.INT:
             if (depth == 1) {
                 Float2D dst = Float2D.create(width, height);
                 Int2D src = (Int2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float value = (float)src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Float3D dst = Float3D.create(depth, width, height);
                 Int3D src = (Int3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float red   = (float)src.get(0,x,y);
                         float green = (float)src.get(1,x,y);
                         float blue  = (float)src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.LONG:
             if (depth == 1) {
                 Float2D dst = Float2D.create(width, height);
                 Long2D src = (Long2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float value = (float)src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Float3D dst = Float3D.create(depth, width, height);
                 Long3D src = (Long3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float red   = (float)src.get(0,x,y);
                         float green = (float)src.get(1,x,y);
                         float blue  = (float)src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.FLOAT:
             if (depth == 1) {
                 Float2D dst = Float2D.create(width, height);
                 Float2D src = (Float2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float value = src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Float3D dst = Float3D.create(depth, width, height);
                 Float3D src = (Float3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float red   = src.get(0,x,y);
                         float green = src.get(1,x,y);
                         float blue  = src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.DOUBLE:
             if (depth == 1) {
                 Float2D dst = Float2D.create(width, height);
                 Double2D src = (Double2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float value = (float)src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Float3D dst = Float3D.create(depth, width, height);
                 Double3D src = (Double3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         float red   = (float)src.get(0,x,y);
                         float green = (float)src.get(1,x,y);
                         float blue  = (float)src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        default:
            throw new IllegalTypeException();
        }
    }
    /**
     * Filter the channels of an image stored as a shaped array to produce
     * a floating point image suitable for inverse problem data processing.
     * <p>
     * This method must be called after converting a buffered image with
     * {@link #imageToShapedArray}.  The method is lazy: the input array is
     * returned if possible.
     * </p>
     * @param image      - The array to filter.
     * @param colorModel - The color model for the result.
     * @return A DoubleArray object with shape {width,height} for a
     * grayscale image, with shape {depth,width,height} for a RGB or RGBA
     * image (depth = 3 or 4 respectively).
     */
    public static DoubleArray filterImageAsDouble(ShapedArray arr, ColorModel colorModel) {
        int type = arr.getType();
        Shape shape = arr.getShape();
        int rank = shape.rank();
        int depth = -1;
        boolean colored = false;
        if (rank == 2) {
            depth = 1;
        } else if (rank == 3) {
            depth = shape.dimension(0);
            if (depth == 3) {
                colored = true;
            } else if (depth == 4 && type == Traits.BYTE) {
                colored = true;
            } else {
                depth = -1;
            }
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Images must be WIDTH x HEIGHT arrays, 3 x WIDTH x HEIGHT arrays or 4 x WIDTH x HEIGHT byte arrays.");

        }
        final int width = shape.dimension(rank - 2);
        final int height = shape.dimension(rank - 1);

        /* Perhaps simply getting a slice is sufficient. */
        ShapedArray view = null;

        if (colorModel == ColorModel.GRAY) {
            if (colored) {
                Double2D dst = Double2D.create(width, height);
                switch (type) {
                case Traits.BYTE: {
                    Byte3D src = (Byte3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)((int)src.get(0,x,y) & 0xFF);
                            double green = (double)((int)src.get(1,x,y) & 0xFF);
                            double blue  = (double)((int)src.get(2,x,y) & 0xFF);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.SHORT: {
                    Short3D src = (Short3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)src.get(0,x,y);
                            double green = (double)src.get(1,x,y);
                            double blue  = (double)src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.INT: {
                    Int3D src = (Int3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)src.get(0,x,y);
                            double green = (double)src.get(1,x,y);
                            double blue  = (double)src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.LONG: {
                    Long3D src = (Long3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)src.get(0,x,y);
                            double green = (double)src.get(1,x,y);
                            double blue  = (double)src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.FLOAT: {
                    Float3D src = (Float3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)src.get(0,x,y);
                            double green = (double)src.get(1,x,y);
                            double blue  = (double)src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                case Traits.DOUBLE: {
                    Double3D src = (Double3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = src.get(0,x,y);
                            double green = src.get(1,x,y);
                            double blue  = src.get(2,x,y);
                            dst.set(x,y, colorToGrey(red, green, blue));
                        }
                    }
                    break;
                }
                default:
                    throw new IllegalTypeException();
                }
                return dst;
            } else {
                view = arr;
            }

       } else if (colorModel == ColorModel.RGB) {

            if (depth == 3) {
                view = arr;
            } else if (depth == 4) {
                view = ((Array3D)arr).view(new Range(0,2), null, null);
            } else {
                /* Make an RGB image from a gray image.  (FIXME: Things could
                 * be much simpler if only we can insert a dimension.) */
                Double3D dst = Double3D.create(3, width, height);
                switch (type) {
                case Traits.BYTE: {
                    Byte2D src = (Byte2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)((int)src.get(x,y) & 0xFF);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.SHORT: {
                    Short2D src = (Short2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.INT: {
                    Int2D src = (Int2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.LONG: {
                    Long2D src = (Long2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.FLOAT: {
                    Float2D src = (Float2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                case Traits.DOUBLE: {
                    Double2D src = (Double2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                        }
                    }
                    break;
                }
                default:
                    throw new IllegalTypeException();
                }
                return dst;
            }

        } else if (colorModel == ColorModel.RGBA) {

            /* Create new floating point RGBA image. */
            Double3D dst = Double3D.create(4, width, height);

            if (depth == 1) {

                /* Make an floating point RGBA image from a gray image. */
                final double opaque = 1.0;
                switch (type) {
                case Traits.BYTE: {
                    Byte2D src = (Byte2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)((int)src.get(x,y) & 0xFF);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.SHORT: {
                    Short2D src = (Short2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.INT: {
                    Int2D src = (Int2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.LONG: {
                    Long2D src = (Long2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.FLOAT: {
                    Float2D src = (Float2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = (double)src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.DOUBLE: {
                    Double2D src = (Double2D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double value = src.get(x,y);
                            dst.set(0,x,y, value);
                            dst.set(1,x,y, value);
                            dst.set(2,x,y, value);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                default:
                    throw new IllegalTypeException();
                }

            } else if (depth == 3) {

                /* Make an floating point RGBA image from a RGB image. */
                final double opaque = 1;
                switch (type) {
                case Traits.BYTE: {
                    Byte3D src = (Byte3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)((int)src.get(0,x,y) & 0xFF);
                            double green = (double)((int)src.get(1,x,y) & 0xFF);
                            double blue  = (double)((int)src.get(2,x,y) & 0xFF);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.SHORT: {
                    Short3D src = (Short3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)src.get(0,x,y);
                            double green = (double)src.get(1,x,y);
                            double blue  = (double)src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.INT: {
                    Int3D src = (Int3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)src.get(0,x,y);
                            double green = (double)src.get(1,x,y);
                            double blue  = (double)src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.LONG: {
                    Long3D src = (Long3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)src.get(0,x,y);
                            double green = (double)src.get(1,x,y);
                            double blue  = (double)src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.FLOAT: {
                    Float3D src = (Float3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = (double)src.get(0,x,y);
                            double green = (double)src.get(1,x,y);
                            double blue  = (double)src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                case Traits.DOUBLE: {
                    Double3D src = (Double3D)arr;
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            double red   = src.get(0,x,y);
                            double green = src.get(1,x,y);
                            double blue  = src.get(2,x,y);
                            dst.set(0,x,y, red);
                            dst.set(1,x,y, green);
                            dst.set(2,x,y, blue);
                            dst.set(3,x,y, opaque);
                        }
                    }
                }
                default:
                    throw new IllegalTypeException();
                }

            } else {

                /* Make an floating point RGBA image from a RGBA byte image. */
                Byte3D src = (Byte3D)arr;
                final double scale = 1.0/255.0;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        double red   = (double)((int)src.get(0,x,y) & 0xFF);
                        double green = (double)((int)src.get(1,x,y) & 0xFF);
                        double blue  = (double)((int)src.get(2,x,y) & 0xFF);
                        double alpha = (double)((int)src.get(3,x,y) & 0xFF)*scale;
                        dst.set(0,x,y, red);
                        dst.set(1,x,y, green);
                        dst.set(2,x,y, blue);
                        dst.set(3,x,y, alpha);
                    }
                }
            }

            return dst;

        } else if (colorModel == ColorModel.RED) {

            view = (depth == 1 ? arr : ((Array3D)arr).slice(0, 0));

        } else if (colorModel == ColorModel.GREEN) {

            view = (depth == 1 ? arr : ((Array3D)arr).slice(1, 0));

        } else if (colorModel == ColorModel.BLUE) {

            view = (depth == 1 ? arr : ((Array3D)arr).slice(2, 0));

        } else if (colorModel == ColorModel.ALPHA) {

            Double2D dst = Double2D.create(width, height);
            if (depth == 4) {
                /* Extract the alpha channel of an RGBA byte image. */
                Byte3D src = (Byte3D)arr;
                final double scale = 1.0/255.0;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        double alpha = (double)((int)src.get(3,x,y) & 0xFF)*scale;
                        dst.set(x,y, alpha);
                    }
                }
            } else {
                dst.fill(1.0);
            }
            return dst;

        } else  {

            throw new IllegalArgumentException("Unkwnon color filter");
        }

        /* If arrive here, we just have to convert the view.   At most what is needed
           is pixel unpacking and data conversion. */
        if (view == arr && type == Traits.DOUBLE) {
            /* Never forget to be lazy. */
            return (DoubleArray)arr;
        }
        shape = view.getShape();
        rank = view.getRank();
        if (rank == 2) {
            depth = 1;
        } else {
            depth = shape.dimension(0);
        }
        switch (type) {
        case Traits.BYTE:
             if (depth == 1) {
                 Double2D dst = Double2D.create(width, height);
                 Byte2D src = (Byte2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double value = (double)((int)src.get(x,y) & 0xFF);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Double3D dst = Double3D.create(depth, width, height);
                 Byte3D src = (Byte3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double red   = (double)((int)src.get(0,x,y) & 0xFF);
                         double green = (double)((int)src.get(1,x,y) & 0xFF);
                         double blue  = (double)((int)src.get(2,x,y) & 0xFF);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.SHORT:
             if (depth == 1) {
                 Double2D dst = Double2D.create(width, height);
                 Short2D src = (Short2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double value = (double)src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Double3D dst = Double3D.create(depth, width, height);
                 Short3D src = (Short3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double red   = (double)src.get(0,x,y);
                         double green = (double)src.get(1,x,y);
                         double blue  = (double)src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.INT:
             if (depth == 1) {
                 Double2D dst = Double2D.create(width, height);
                 Int2D src = (Int2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double value = (double)src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Double3D dst = Double3D.create(depth, width, height);
                 Int3D src = (Int3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double red   = (double)src.get(0,x,y);
                         double green = (double)src.get(1,x,y);
                         double blue  = (double)src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.LONG:
             if (depth == 1) {
                 Double2D dst = Double2D.create(width, height);
                 Long2D src = (Long2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double value = (double)src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Double3D dst = Double3D.create(depth, width, height);
                 Long3D src = (Long3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double red   = (double)src.get(0,x,y);
                         double green = (double)src.get(1,x,y);
                         double blue  = (double)src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.FLOAT:
             if (depth == 1) {
                 Double2D dst = Double2D.create(width, height);
                 Float2D src = (Float2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double value = (double)src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Double3D dst = Double3D.create(depth, width, height);
                 Float3D src = (Float3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double red   = (double)src.get(0,x,y);
                         double green = (double)src.get(1,x,y);
                         double blue  = (double)src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        case Traits.DOUBLE:
             if (depth == 1) {
                 Double2D dst = Double2D.create(width, height);
                 Double2D src = (Double2D)view;
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double value = src.get(x,y);
                         dst.set(x,y, value);
                     }
                 }
                 return dst;
             } else {
                 Double3D dst = Double3D.create(depth, width, height);
                 Double3D src = (Double3D)view;
                 if (depth != 3) {
                     throw new IllegalArgumentException("assertion failed");
                 }
                 for (int y = 0; y < height; ++y) {
                     for (int x = 0; x < width; ++x) {
                         double red   = src.get(0,x,y);
                         double green = src.get(1,x,y);
                         double blue  = src.get(2,x,y);
                         dst.set(0,x,y, red);
                         dst.set(1,x,y, green);
                         dst.set(2,x,y, blue);
                     }
                 }
                 return dst;
             }
        default:
            throw new IllegalTypeException();
        }
    }

}

/*
 * Local Variables:
 * mode: Java
 * tab-width: 8
 * indent-tabs-mode: nil
 * c-basic-offset: 4
 * fill-column: 78
 * coding: utf-8
 * ispell-local-dictionary: "american"
 * End:
 */
