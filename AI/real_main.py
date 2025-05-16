from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel
import google.generativeai as genai
from vertexai.preview.vision_models import ImageGenerationModel
import vertexai
import os
import base64
from io import BytesIO

app = FastAPI()

# CORS settings
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Retrieving API key from environment variables
genai.configure(api_key=os.getenv("GENAI_API_KEY"))


#1. Gemini Vision: Clothing Image Classification
vision_model = genai.GenerativeModel("gemini-1.5-flash")

vision_prompt = """
This is a photo of a clothing item, which is either a top or a bottom.

Your task is to classify this item strictly as **one** of the following categories:

Knit, Sweatshirt, Shirt, Zip-up Hoodie, Puffer Jacket, Jacket, Shorts, Jeans, Sweatpants, Dress, Skirt

⚠️ Do not generate any sentences, explanations, or additional words.  
⚠️ You must reply with **only one word** from the list above.  
⚠️ If the item doesn't match clearly, choose the most similar one from the list.
"""

@app.post("/classify-clothing/")
async def classify_clothing(file: UploadFile = File(...)):
    image_data = await file.read()
    encoded_image = base64.b64encode(image_data).decode("utf-8")
    gemini_image = {
        "inline_data": {
            "mime_type": file.content_type,
            "data": encoded_image
        }
    }
    response = vision_model.generate_content([vision_prompt, gemini_image])
    result = response.text.strip()
    return JSONResponse(content={"category": result})

#2. 3 Recommended Reform Products
text_model = genai.GenerativeModel("gemini-2.0-flash")

class ClothingRequest(BaseModel):
    clothing_info: str

@app.post("/recommend")
def recommend_items(data: ClothingRequest):
    prompt = f"""
You are an upcycling expert.

Your task is to suggest 3 realistic product ideas that can be made from the following item:
- "{data.clothing_info}"

📌 Conditions:
- Each suggestion must be a practical product that could realistically be created from that item.
- Include the original item description in each result naturally.
- Output ONLY the product names. No explanations, no intros, no closing statements.
- Output MUST follow this format EXACTLY:

1. ...
2. ...
3. ...
    """
    try:
        response = text_model.generate_content(prompt)
        result = response.text.strip().split("\n")
        return {"recommendations": result}
    except Exception as e:
        return {"error": str(e)}



#3. Vertex AI: Reform Image Generation
# ✅ Cloud Run automatically uses Application Default Credentials
vertexai.init(
    project="generate-imagen-api",
    location="us-central1"
)

image_model = ImageGenerationModel.from_pretrained("imagen-3.0-generate-001")

class PromptRequest(BaseModel):
    option: str

@app.post("/generate-preview")
def generate_image(prompt_req: PromptRequest):
    prompt_text = (
        f"A {prompt_req.option} made from recycled clothes, designed to be eco-friendly and handmade. "
        "Displayed in a clean studio setting with a minimal, neutral background and no distractions."
    )
    images = image_model.generate_images(prompt=prompt_text, number_of_images=1, aspect_ratio="1:1")
    buffered = BytesIO()
    images[0]._pil_image.save(buffered, format="PNG")
    img_base64 = base64.b64encode(buffered.getvalue()).decode("utf-8")
    return {
        "option": prompt_req.option,
        "prompt": prompt_text,
        "image_base64": img_base64
    }
