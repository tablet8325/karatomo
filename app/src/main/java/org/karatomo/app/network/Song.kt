package org.karatomo.app.network

import java.io.Serializable

/**
 * [Song] 데이터 클래스
 * 4개 브랜드의 번호를 통합 관리하며, 보관함 상세 정보 및 정규화 로직을 지원합니다.
 * 기존 네트워크 통신용 필드와 호환성을 유지하면서 확장되었습니다.
 */
data class Song(
    val title: String,            // 정규화된 제목 (괄호 제거 등)
    val originalTitle: String,    // API 원본 제목 (필요 시 보존)
    val singer: String,
    val composer: String = "",
    val lyricist: String = "",
    val addedDate: String = "",   // 보관함 추가 일자 (yyyy-MM-dd HH:mm)
    
    // 브랜드별 번호 (보관함 통합 관리용)
    var noTj: String? = null,
    var noKy: String? = null,
    var noDam: String? = null,
    var noJoy: String? = null,
    
    // 기존 API 필드와의 호환성을 위한 필드 (기능 유지용)
    val brand: String? = null,
    val no: String? = null
) : Serializable

/**
 * [Playlist] (탭) 데이터 클래스
 * 보관함의 각 탭 정보를 관리합니다.
 */
data class Playlist(
    var name: String,
    val isDefault: Boolean = false, // 기본 탭 여부 (삭제 불가)
    val songs: MutableList<Song> = mutableListOf()
) : Serializable
