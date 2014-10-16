package de.fau.cs.mad.fly.graphics.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

/**
 * Created by tschaei on 22.07.14.
 */
public class FlyShaderProvider extends BaseShaderProvider {
    
    @Override
    public Shader getShader(Renderable renderable) {
        Shader suggestedShader = renderable.shader;
        if (suggestedShader != null && suggestedShader.canRender(renderable))
            return suggestedShader;
        for (Shader shader : shaders) {
            if (shader.canRender(renderable))
                return shader;
        }
        final Shader shader = createShader(renderable);
        shader.init();
        shaders.add(shader);
        return shader;
    }
    
    @Override
    protected Shader createShader(Renderable renderable) {
        if (renderable.material.has(TextureAttribute.Diffuse)) {
            return new FlyTextureShader(renderable);
        }
        return new FlyShader(renderable);
    }
}
