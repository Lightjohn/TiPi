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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import mitiv.array.ArrayUtils;
import mitiv.array.ShapedArray;

/**
 * This class deals with identifying various data format.
 * 
 * @author Éric Thiébaut.
 *
 */
public enum DataFormat {

    PNM("PNM", "Portable anymap (PBM/PGM/PPM/PNM) image.", new String[]{"pnm", "ppm", "pgm", "pbm"}),
    JPEG("JPEG", "JPEG image.", new String[]{"jpg", "jpeg"}),
    PNG("PNG", "Portable Network Graphic (PNG) image.", new String[]{"png"}),
    GIF("GIF", "GIF image.", new String[]{"gif"}),
    BMP("BMP", "BMP image.", new String[]{"bmp"}),
    WBMP("WBMP", "Wireless Bitmap (WBMP) image format.", new String[]{"wbmp"}),
    TIFF("TIFF", "TIFF image format.", new String[]{"tiff", "tif"}),
    FITS("FITS", "Flexible Image Transport System (FITS) format.", new String[]{"fits", "fts", "fit"}),
    MDA("MDA", "Multi-dimensional array (MDA) format.", new String[]{"mda"});

    private final String identifier;

    private final String description;

    private final String[] extensions;

    private DataFormat(String ident, String descr, String[] extensions) {
        this.identifier = ident;
        this.description = descr;
        this.extensions = extensions;
    }

    /**
     * Get the name of the data file format.
     * 
     * For image format supported by Java, the format name is suitable for
     * {@link ImageIO#write}.
     * @return The name of the format.
     */
    public String identifier() {
        return identifier;
    }

    /** Get the description of the file format. */
    public String description() {
        return description;
    }

    /** Get the recognized file name extensions for the format. */
    public String[] extensions() {
        return extensions;
    }

    /** Get a string representation of the color model. */
    @Override
    public String toString() {
        return description;
    }

    /**
     * Check whether a suffix matches recognized file name extensions.
     * 
     * @param suffix - The suffix to check.
     * @return A boolean value.
     */
    public boolean match(String suffix) {
        if (suffix != null && suffix.length() > 0) {
            int n = (extensions == null ? 0 : extensions.length);
            for (int k = 0; k < n; ++k) {
                if (suffix.equalsIgnoreCase(extensions[k])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Guess data format from the extension of the file name.
     * <p>
     * This function examines the extension of the file name
     * to determine the data format.
     * </p>
     * @param fileName - The name of the file.
     * @return A file format identifier: {@code null} if the
     *         format is not recognized; otherwise {@link #PNM},
     *         {@link #JPEG}, {@link #PNG}, {@link #TIFF},
     *         {@link #FITS}, {@link #GIF}, or {@link #MDA}.
     */
    public static final DataFormat guessFormat(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return null;
        }
        String suffix = fileName.substring(index + 1);
        if (MDA.match(suffix)) {
            return MDA;
        }
        if (PNG.match(suffix)) {
            return PNG;
        }
        if (JPEG.match(suffix)) {
            return JPEG;
        }
        if (PNM.match(suffix)) {
            return PNM;
        }
        if (TIFF.match(suffix)) {
            return TIFF;
        }
        if (FITS.match(suffix)) {
            return FITS;
        }
        if (BMP.match(suffix)) {
            return BMP;
        }
        if (WBMP.match(suffix)) {
            return WBMP;
        }
        if (FITS.match(suffix)) {
            return FITS;
        }
        if (GIF.match(suffix)) {
            return GIF;
        }
        return null;
    }


    /**
     * Guess data format from the given options or from the extension of
     * the file name.
     * <p>
     * It a preferred data format is specified in the options, this format
     * is returned; otherwise, the extension of the file name is examined
     * to determine the data format.
     * </p>
     * @param fileName - The name of the file.
     * @return A file format identifier: {@code null} if the
     *         format is not recognized; otherwise {@link #PNM},
     *         {@link #JPEG}, {@link #PNG}, {@link #TIFF},
     *         {@link #FITS}, {@link #GIF}, or {@link #MDA}.
     */
    public static final DataFormat guessFormat(String fileName,
            FormatOptions opts) {
        DataFormat format = (opts == null ? null : opts.getDataFormat());
        if (format != null) {
            return format;
        } else {
            return guessFormat(fileName);
        }
    }

    /**
     * Guess data format from a few magic bytes.
     * <p>
     * This function examines the few next bytes available from the data
     * stream to determine the data format.  In any case, the stream position
     * is left where it was prior to calling the function (i.e. there is no data
     * consumption).
     * 
     * @param stream - The input data stream.
     * 
     * @return A file format identifier (see {@link #guessFormat(String)}),
     *         or {@code null} if the format is not recognized,.
     * @throws IOException
     */
    public static final DataFormat guessFormat(BufferedInputDataStream stream)
            throws IOException {
        int preserved = stream.insure(80);
        if (preserved < 2) {
            return null;
        }
        DataFormat format = null;
        stream.mark();
        try {
            int length = Math.min(preserved, 80);
            byte[] magic = new byte[length];
            length = stream.read(magic, 0, length);
            if (length < 2) {
                return null;
            }
            if (length >= 2 && matchMagic(magic, '\377', '\330')) {
                format = JPEG;
            } else if (length >= 4 && matchMagic(magic, '\211', 'P', 'N', 'G')) {
                format = PNG;
            } else if (length >= 4 && matchMagic(magic, 'M', 'M', '\000', '\052')) {
                format = TIFF;
            } else if (length >= 4 && matchMagic(magic, 'I', 'I', '\052', '\000')) {
                format = TIFF;
            } else if (length >= 4 && matchMagic(magic, 'G', 'I', 'F', '8')) {
                format = GIF;
            } else if (length >= 3 && magic[0] == (byte)'P' && isSpace(magic[2])
                    && (byte)'1' <= magic[1] && magic[1] <= (byte)'6') {
                /* "P1" space = ascii PBM (portable bitmap)
                 * "P2" space = ascii PGM (portable graymap)
                 * "P3" space = ascii PPM (portable pixmap)
                 * "P4" space = raw PBM
                 * "P5" space = raw PGM
                 * "P6" space = raw PPM
                 */
                format = PNM;
            } else if (length >= 3
                    && matchMagic(magic, 'S', 'I', 'M', 'P', 'L', 'E', ' ', ' ', '=')) {
                format = FITS;
            }
        } catch (IOException ex) {
            throw ex;
        } finally{
            stream.reset();
        }
        return format;
    }

    private static final boolean matchMagic(byte[] b,
            char c0, char c1) {
        return ((b[0] == (byte)c0) && (b[1] == (byte)c1));
    }

    private static final boolean matchMagic(byte[] b,
            char c0, char c1, char c2, char c3) {
        return ((b[0] == (byte)c0) && (b[1] == (byte)c1) &&
                (b[2] == (byte)c2) && (b[3] == (byte)c3));
    }

    private static final boolean matchMagic(byte[] b,
            char c0, char c1, char c2, char c3,
            char c4, char c5, char c6, char c7, char c8) {
        return ((b[0] == (byte)c0) && (b[1] == (byte)c1) &&
                (b[2] == (byte)c2) && (b[3] == (byte)c3) &&
                (b[4] == (byte)c4) && (b[5] == (byte)c5) &&
                (b[6] == (byte)c6) && (b[7] == (byte)c7) &&
                (b[8] == (byte)c8));
    }

    private static final boolean isSpace(byte s) {
        return (s == (byte)' ' || s == (byte)'\n' || s == (byte)'\r' || s == (byte)'\t');
    }

    private static void fatal(String reason) {
        throw new IllegalArgumentException(reason);
    }

    /**
     * Load formatted data from afile.
     * @param fileName - The name of the input file.
     * @param colorModel - The model for dealing with color images.
     * @param description - A description for error messages.
     * @return
     */
    public static ShapedArray load(String fileName, ColorModel colorModel, String description) {
        ShapedArray arr = null;
        DataFormat format = DataFormat.guessFormat(fileName);
        try {
            if (format == MDA) {
                arr = MdaFormat.load(fileName);
            } else {
                arr = ArrayUtils.imageAsDouble(ImageIO.read(new File(fileName)), colorModel);
            }
        } catch (Exception e) {
            fatal("Error while reading " + description + "(" + e.getMessage() +").");
        }
        return arr;
    }

    /**
     * Save data to file.
     * <p>
     * The file format is guessed from the extension of the file name and default
     * options are used to encode the file.
     * </p>
     * @param arr - The data to save.
     * @param fileName - The destination file.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void save(ShapedArray arr, String fileName)
            throws FileNotFoundException, IOException {
        save(arr, fileName, new FormatOptions());
    }

    /**
     * Save data to file with given options.
     * <p>
     * The file format is guessed from the extension of the file name and default
     * options are used to encode the file.
     * </p>
     * @param arr - The data to save.
     * @param fileName - The destination file.
     * @param opts - Options for encoding the data.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void save(ShapedArray arr, String fileName,
            FormatOptions opts) throws FileNotFoundException, IOException {
        DataFormat format = DataFormat.guessFormat(fileName);
        String identifier = null;
        switch (format) {
        //case PNM:
        case JPEG:
        case PNG:
        case GIF:
        case BMP:
        case WBMP:
            //case DataFormat.TIFF:
            //case DataFormat.FITS:
            identifier = format.identifier();
            break;
        case MDA:
            MdaFormat.save(arr, fileName);
            return;
        default:
            identifier = null;
        }
        if (identifier == null) {
            fatal("Unknown/unsupported format name.");
        }
        int depth, width, height, rank = arr.getRank();
        if (rank == 2) {
            depth = 1;
            width = arr.getDimension(0);
            height = arr.getDimension(1);
        } else if (rank == 3) {
            depth = arr.getDimension(0);
            width = arr.getDimension(1);
            height = arr.getDimension(2);
        } else {
            depth = 0;
            width = 0;
            height = 0;
            fatal("Expecting 2D array as image.");
        }
        double[] data = arr.toDouble().flatten();
        BufferedImage buf = ArrayUtils.doubleAsBuffered(data, depth, width, height, opts);
        ImageIO.write(buf, identifier, new File(fileName));
    }

    public static void main(String[] args) {
        String[] str;
        str = ImageIO.getReaderFormatNames();
        System.out.format("Format names understood by registered readers:\n");
        for (int i = 0; i < str.length; ++i) {
            System.out.format("  - %s\n", str[i]);
        }

        str = ImageIO.getReaderFileSuffixes();
        System.out.format("\nImage suffixes understood by registered readers:\n");
        for (int i = 0; i < str.length; ++i) {
            System.out.format("  - %s\n", str[i]);
        }

        str = ImageIO.getWriterFormatNames();
        System.out.format("\nFormat names understood by registered writers:\n");
        for (int i = 0; i < str.length; ++i) {
            System.out.format("  - %s\n", str[i]);
        }

        str = ImageIO.getWriterFileSuffixes();
        System.out.format("\nImage suffixes understood by registered writers:\n");
        for (int i = 0; i < str.length; ++i) {
            System.out.format("  - %s\n", str[i]);
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
