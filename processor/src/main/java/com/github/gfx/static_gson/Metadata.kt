package com.github.gfx.static_gson

class Metadata(
    /**
     * A kind of the metadata this annotation encodes. Kotlin compiler recognizes the following kinds (see KotlinClassHeader.Kind):
     *
     * 1 Class
     * 2 File
     * 3 Synthetic class
     * 4 Multi-file class facade
     * 5 Multi-file class part
     *
     * The class file with a kind not listed here is treated as a non-Kotlin file.
     */
    val k: Int = 1,
    /**
     * The version of the metadata provided in the arguments of this annotation.
     */
    val mv: IntArray = intArrayOf(),
    /**
     * The version of the bytecode interface (naming conventions, signatures) of the class file annotated with this annotation.
     */
    val bv: IntArray = intArrayOf(),
    /**
     * Metadata in a custom format. The format may be different (or even absent) for different kinds.
     */
    val d1: Array<String> = arrayOf(),
    /**
     * An addition to [d1]: array of strings which occur in metadata, written in plain text so that strings already present
     * in the constant pool are reused. These strings may be then indexed in the metadata by an integer index in this array.
     */
    val d2: Array<String> = arrayOf(),
    /**
     * An extra string. For a multi-file part class, internal name of the facade class.
     */
    val xs: String = "",
    /**
     * An extra int. Bits of this number represent the following flags:
     *
     * 0 - this is a multi-file class facade or part, compiled with `-Xmultifile-parts-inherit`.
     * 1 - this class file is compiled by a pre-release version of Kotlin and is not visible to release versions.
     * 2 - this class file is a compiled Kotlin script source file (.kts).
     */
    @SinceKotlin("1.1")
    val xi: Int = 0
)
