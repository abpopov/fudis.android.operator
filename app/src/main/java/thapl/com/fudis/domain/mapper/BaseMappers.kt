package thapl.com.fudis.domain.mapper

interface BaseMapperNonNull<in A, out B> {
    fun map(type: A): B
}

interface BaseMapperNullable<in A, out B> {
    fun map(type: A?): B?
}

interface BaseMapperSafe<in A, out B> {
    fun map(type: A?): B
}