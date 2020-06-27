/*
 * Copyright (C) 2016 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.engine.renderware;

/**
 *
 * @author Thinh Pham
 */
public enum RpType {
    
    // Core
    Struct (0x00000001, "Struct", false),
    String (0x00000002, "String", false),
    Extension (0x00000003, "Extension", true),
    Camera (0x00000005, "Camera", true),
    Texture (0x00000006, "Texture", true),
    Material (0x00000007, "Material", true),
    MaterialList (0x00000008, "Material List", true),
    AtomicSection (0x00000009, "Atomic Section", true),
    PlaneSection (0x0000000a, "Plane Section", true),
    World (0x0000000b, "World", true),
    Spline (0x0000000c, "Spline", true),
    Matrix (0x0000000d, "Matrix", true),
    FrameList (0x0000000e, "Frame List", true),
    Geometry (0x0000000f, "Geometry", true),
    Clump (0x00000010, "Clump", true),
    Light (0x00000012, "Light", true),
    UnicodeString (0x00000013, "Unicode String", true),
    Atomic (0x00000014, "Atomic", true),
    TextureNative (0x00000015, "Texture Native", true),
    TextureDictionary (0x00000016, "Texture Dictionary", true),
    AnimationDatabase (0x00000017, "Animation Database", true),
    Image (0x00000018, "Image", true),
    SkinAnimation (0x00000019, "Skin Animation", true),
    GeometryList (0x0000001a, "Geometry List", true),
    AnimAnimation (0x0000001b, "Anim Animation", false),
    Team (0x0000001c, "Team", true),
    Crowd (0x0000001d, "Crowd", true),
    DeltaMorphAnimation (0x0000001e, "Delta Morph Animation", true),
    RightToRender (0x0000001f, "Right To Render", false),
    MTENative (0x00000020, "MultiTexture Effect Native", true),
    MTEDictionary (0x00000021, "MultiTexture Effect Dictionary", true),
    TeamDictionary (0x00000022, "Team Dictionary", true),
    PITextureDictionary (0x00000023, "Platform Independent Texture Dictionary", true),
    TableofContents (0x00000024, "Table of Contents", true),
    ParticleStandardGlobalData (0x00000025, "Particle Standard Global Data", true),
    AltPipe (0x00000026, "AltPipe", true),
    PlatformIndependentPeds (0x00000027, "Platform Independent Peds", true),
    PatchMesh (0x00000028, "Patch Mesh", true),
    ChunkGroupStart (0x00000029, "Chunk Group Start", true),
    ChunkGroupEnd (0x0000002a, "Chunk Group End", true),
    UVAnimationDictionary (0x0000002b, "UV Animation Dictionary", true),
    CollTree (0x0000002c, "Coll Tree", true),
    // Toolkit
    MetricsPLG (0x00000101, "Metrics PLG", true),
    SplinePLG (0x00000102, "Spline PLG", true),
    StereoPLG (0x00000103, "Stereo PLG", true),
    VRMLPLG (0x00000104, "VRML PLG", true),
    MorphPLG (0x00000105, "Morph PLG", false),
    PVSPLG (0x00000106, "PVS PLG", true),
    MemoryLeakPLG (0x00000107, "Memory Leak PLG", true),
    AnimationPLG (0x00000108, "Animation PLG", true),
    GlossPLG (0x00000109, "Gloss PLG", true),
    LogoPLG (0x0000010a, "Logo PLG", true),
    MemoryInfoPLG (0x0000010b, "Memory Info PLG", true),
    RandomPLG (0x0000010c, "Random PLG", true),
    PNGImagePLG (0x0000010d, "PNG Image PLG", true),
    BonePLG (0x0000010e, "Bone PLG", true),
    VRMLAnimPLG (0x0000010f, "VRML Anim PLG", true),
    SkyMipmapVal (0x00000110, "Sky Mipmap Val", false),
    MRMPLG (0x00000111, "MRM PLG", true),
    LODAtomicPLG (0x00000112, "LOD Atomic PLG", true),
    MEPLG (0x00000113, "ME PLG", true),
    LightmapPLG (0x00000114, "Lightmap PLG", true),
    RefinePLG (0x00000115, "Refine PLG", true),
    SkinPLG (0x00000116, "Skin PLG", false),
    LabelPLG (0x00000117, "Label PLG", true),
    ParticlesPLG (0x00000118, "Particles PLG", false),
    GeomTXPLG (0x00000119, "GeomTX PLG", true),
    SynthCorePLG (0x0000011a, "Synth Core PLG", true),
    STQPPPLG (0x0000011b, "STQPP PLG", true),
    PartPPPLG (0x0000011c, "Part PP PLG", true),
    CollisionPLG (0x0000011d, "Collision PLG", true),
    HAnimPLG (0x0000011e, "HAnim PLG", false),
    UserDataPLG (0x0000011f, "User Data PLG", true),
    MaterialEffectsPLG (0x00000120, "Material Effects PLG", false),
    ParticleSystemPLG (0x00000121, "Particle System PLG", true),
    DeltaMorphPLG (0x00000122, "Delta Morph PLG", true),
    PatchPLG (0x00000123, "Patch PLG", true),
    TeamPLG (0x00000124, "Team PLG", true),
    CrowdPPPLG (0x00000125, "Crowd PP PLG", true),
    MipSplitPLG (0x00000126, "Mip Split PLG", true),
    AnisotropyPLG (0x00000127, "Anisotropy PLG", true),
    GCNMaterialPLG (0x00000129, "GCN Material PLG", true),
    GeometricPVSPLG (0x0000012a, "Geometric PVS PLG", true),
    XBOXMaterialPLG (0x0000012b, "XBOX Material PLG", true),
    MultiTexturePLG (0x0000012c, "Multi Texture PLG", true),
    ChainPLG (0x0000012d, "Chain PLG", true),
    ToonPLG (0x0000012e, "Toon PLG", true),
    PTankPLG (0x0000012f, "PTank PLG", true),
    ParticleStandardPLG (0x00000130, "Particle Standard PLG", true),
    PDSPLG (0x00000131, "PDS PLG", true),
    PrtAdvPLG (0x00000132, "PrtAdv PLG", true),
    NormalMapPLG (0x00000133, "Normal Map PLG", true),
    ADCPLG (0x00000134, "ADC PLG", true),
    UVAnimationPLG (0x00000135, "UV Animation PLG", true),
    CharacterSetPLG (0x00000180, "Character Set PLG", true),
    NOHSWorldPLG (0x00000181, "NOHS World PLG", true),
    ImportUtilPLG (0x00000182, "Import Util PLG", true),
    SlerpPLG (0x00000183, "Slerp PLG", true),
    OptimPLG (0x00000184, "Optim PLG", true),
    TLWorldPLG (0x00000185, "TL World PLG", true),
    DatabasePLG (0x00000186, "Database PLG", true),
    RaytracePLG (0x00000187, "Raytrace PLG", true),
    RayPLG (0x00000188, "Ray PLG", true),
    LibraryPLG (0x00000189, "Library PLG", true),
    PLG2D (0x00000190, "2D PLG", true),
    TileRenderPLG (0x00000191, "Tile Render PLG", true),
    JPEGImagePLG (0x00000192, "JPEG Image PLG", true),
    TGAImagePLG (0x00000193, "TGA Image PLG", true),
    GIFImagePLG (0x00000194, "GIF Image PLG", true),
    QuatPLG (0x00000195, "Quat PLG", true),
    SplinePVSPLG (0x00000196, "Spline PVS PLG", true),
    MipmapPLG (0x00000197, "Mipmap PLG", true),
    MipmapKPLG (0x00000198, "MipmapK PLG", true),
    Font2D (0x00000199, "2D Font", true),
    IntersectionPLG (0x0000019a, "Intersection PLG", true),
    TIFFImagePLG (0x0000019b, "TIFF Image PLG", true),
    PickPLG (0x0000019c, "Pick PLG", true),
    BMPImagePLG (0x0000019d, "BMP Image PLG", true),
    RASImagePLG (0x0000019e, "RAS Image PLG", true),
    SkinFXPLG (0x0000019f, "Skin FX PLG", true),
    VCATPLG (0x000001a0, "VCAT PLG", true),
    Path2D (0x000001a1, "2D Path", true),
    Brush2D (0x000001a2, "2D Brush", true),
    Object2D (0x000001a3, "2D Object", true),
    Shape2D (0x000001a4, "2D Shape", true),
    Scene2D (0x000001a5, "2D Scene", true),
    PickRegion2D (0x000001a6, "2D Pick Region", true),
    ObjectString2D (0x000001a7, "2D Object String", true),
    AnimationPLG2D (0x000001a8, "2D Animation PLG", true),
    Animation2D (0x000001a9, "2D Animation", true),
    Keyframe2D (0x000001b0, "2D Keyframe", true),
    Maestro2D (0x000001b1, "2D Maestro", true),
    Barycentric (0x000001b2, "Barycentric", true),
    PITextureDictionaryTK (0x000001b3, "Platform Independent Texture Dictionary TK", true),
    TOCTK (0x000001b4, "TOC TK", true),
    TPLTK (0x000001b5, "TPL TK", true),
    AltPipeTK (0x000001b6, "AltPipe TK", true),
    AnimationTK (0x000001b7, "Animation TK", true),
    SkinSplitTookit (0x000001b8, "Skin Split Tookit", true),
    CompressedKeyTK (0x000001b9, "Compressed Key TK", true),
    GeometryConditioningPLG (0x000001ba, "Geometry Conditioning PLG", true),
    WingPLG (0x000001bb, "Wing PLG", true),
    GenericPipelineTK (0x000001bc, "Generic Pipeline TK", true),
    LightmapConversionTK (0x000001bd, "Lightmap Conversion TK", true),
    FilesystemPLG (0x000001be, "Filesystem PLG", true),
    DictionaryTK (0x000001bf, "Dictionary TK", true),
    UVAnimationLinear (0x000001c0, "UV Animation Linear", true),
    UVAnimationParameter (0x000001c1, "UV Animation Parameter", true),
    // World
    BinMeshPLG (0x0000050e, "Bin Mesh PLG", false),
    NativeDataPLG (0x00000510, "Native Data PLG", true),
    // Miscellaneous
    ZModelerLock (0x0000f21e, "ZModeler Lock", true),
    // Rockstar's Custom Sections
    AtomicVisibilityDistance (0x0253f200, "Atomic Visibility Distance", true),
    ClumpVisibilityDistance (0x0253f201, "Clump Visibility Distance", true),
    FrameVisibilityDistance (0x0253f202, "Frame Visibility Distance", true),
    PipelineSet (0x0253f2f3, "Pipeline Set", false),
    Unused5 (0x0253f2f4, "Unused 5", true),
    TexDictionaryLink (0x0253f2f5, "TexDictionary Link", true),
    SpecularMaterial (0x0253f2f6, "Specular Material", false),
    Unused8 (0x0253f2f7, "Unused 8", true),
    Effect2D (0x0253f2f8, "2D Effect", false),
    ExtraVertColour (0x0253f2f9, "Extra Vert Colour", false),
    CollisionModel (0x0253f2fa, "Collision Model", false),
    GTAHAnim (0x0253f2fb, "GTA HAnim", true),
    ReflectionMaterial (0x0253f2fc, "Reflection Material", false),
    Breakable (0x0253f2fd, "Breakable", false),
    NodeName (0x0253f2fe, "Node Name", false),
    Unused16 (0x0253f2ff, "Unused 16", true),
    Null (0x00000000, "NULL", true),
    Unknow (null, "Unknow", true);
    
    public final Integer id;
    public final String name;
    public final boolean isContainer;
    
    private RpType(Integer id, String name, boolean isContainer) {
        this.id = id;
        this.name = name;
        this.isContainer = isContainer;
    }
    
    public static RpType getType(int id) {
        for (RpType t : values()) {
            if (t.id == id)
                return t;
        }
        return Unknow;
    }
}
